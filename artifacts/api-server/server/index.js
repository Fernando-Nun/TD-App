import crypto from "node:crypto";
import express from "express";
import pg from "pg";
import { pathToFileURL } from "node:url";
import { ReplitConnectors } from "@replit/connectors-sdk";
import { mergeSnapshots } from "./merge.js";

const { Pool } = pg;
const app = express();
const pool = new Pool({ connectionString: process.env.DATABASE_URL });
const connectors = new ReplitConnectors();
const secret = process.env.SESSION_SECRET;
if (!secret) throw new Error("SESSION_SECRET is required");

app.use(express.json({ limit: "1mb" }));
app.set("trust proxy", 1);
app.locals.sendPasswordResetEmail = sendPasswordResetEmail;

const hash = (value) => crypto.createHmac("sha256", secret).update(value).digest("hex");
const passwordHash = (password, salt) =>
  crypto.scryptSync(password, salt, 64).toString("hex");

app.get("/health", (_req, res) => res.json({ ok: true }));
app.get("/api/healthz", (_req, res) => res.json({ status: "ok" }));

app.post("/api/auth/register", async (req, res) => {
  const email = String(req.body.email ?? "").trim().toLowerCase();
  const password = String(req.body.password ?? "");
  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email) || password.length < 8) {
    return res.status(400).json({ error: "Usa un correo válido y una contraseña de 8 caracteres." });
  }
  const salt = crypto.randomBytes(16).toString("hex");
  try {
    const result = await pool.query(
      "INSERT INTO sync_users(id,email,password_hash) VALUES($1,$2,$3) RETURNING id",
      [crypto.randomUUID(), email, `${salt}:${passwordHash(password, salt)}`],
    );
    return res.status(201).json(await createSession(result.rows[0].id));
  } catch (error) {
    if (error.code === "23505") return res.status(409).json({ error: "Ese correo ya está registrado." });
    throw error;
  }
});

app.post("/api/auth/login", async (req, res) => {
  const email = String(req.body.email ?? "").trim().toLowerCase();
  const password = String(req.body.password ?? "");
  const result = await pool.query("SELECT id,password_hash FROM sync_users WHERE email=$1", [email]);
  const user = result.rows[0];
  if (!user) return res.status(401).json({ error: "Correo o contraseña incorrectos." });
  const [salt, expected] = user.password_hash.split(":");
  const actual = passwordHash(password, salt);
  if (expected.length !== actual.length ||
      !crypto.timingSafeEqual(Buffer.from(expected), Buffer.from(actual))) {
    return res.status(401).json({ error: "Correo o contraseña incorrectos." });
  }
  res.json(await createSession(user.id));
});

app.post("/api/auth/password-reset/request", async (req, res) => {
  const email = String(req.body.email ?? "").trim().toLowerCase();
  const accepted = {
    message: "Si existe una cuenta con ese correo, recibirás un código que caduca en 15 minutos.",
  };
  if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(email)) {
    return res.status(202).json(accepted);
  }

  let code;
  const client = await pool.connect();
  try {
    await client.query("BEGIN");
    const accountAllowed = await consumeResetLimit(client, `account:${email}`, 3);
    const sourceAllowed = await consumeResetLimit(client, `source:${req.ip}`, 20);
    const result = await client.query("SELECT id FROM sync_users WHERE email=$1 FOR UPDATE", [email]);
    const user = result.rows[0];
    if (user && accountAllowed && sourceAllowed) {
      code = crypto.randomBytes(9).toString("base64url").toUpperCase();
      const expiresAt = new Date(Date.now() + 15 * 60 * 1000);
      await client.query("DELETE FROM password_reset_codes WHERE user_id=$1 OR expires_at<=now()", [user.id]);
      await client.query(
        "INSERT INTO password_reset_codes(code_hash,user_id,expires_at) VALUES($1,$2,$3)",
        [hash(code), user.id, expiresAt],
      );
    }
    await client.query("COMMIT");
  } catch (error) {
    await client.query("ROLLBACK");
    throw error;
  } finally {
    client.release();
  }
  if (code) {
    try {
      await req.app.locals.sendPasswordResetEmail(email, code);
    } catch (error) {
      console.error("Could not send password reset email", error);
    }
  }
  res.status(202).json(accepted);
});

app.post("/api/auth/password-reset/confirm", async (req, res) => {
  const email = String(req.body.email ?? "").trim().toLowerCase();
  const code = String(req.body.code ?? "").trim().toUpperCase();
  const password = String(req.body.password ?? "");
  if (password.length < 8) {
    return res.status(400).json({ error: "La nueva contraseña debe tener al menos 8 caracteres." });
  }

  const client = await pool.connect();
  try {
    await client.query("BEGIN");
    const result = await client.query(
      `SELECT c.code_hash,c.user_id
       FROM password_reset_codes c
       JOIN sync_users u ON u.id=c.user_id
       WHERE u.email=$1 AND c.code_hash=$2 AND c.used_at IS NULL AND c.expires_at>now()
       FOR UPDATE OF c`,
      [email, hash(code)],
    );
    const reset = result.rows[0];
    if (!reset) {
      await client.query("ROLLBACK");
      return res.status(400).json({ error: "El código es incorrecto o ha caducado." });
    }
    const salt = crypto.randomBytes(16).toString("hex");
    await client.query("UPDATE sync_users SET password_hash=$1 WHERE id=$2", [
      `${salt}:${passwordHash(password, salt)}`,
      reset.user_id,
    ]);
    await client.query("UPDATE password_reset_codes SET used_at=now() WHERE code_hash=$1", [reset.code_hash]);
    await client.query("DELETE FROM sync_sessions WHERE user_id=$1", [reset.user_id]);
    await client.query("COMMIT");
    res.json({ message: "Contraseña actualizada. Inicia sesión con tu nueva contraseña." });
  } catch (error) {
    await client.query("ROLLBACK");
    throw error;
  } finally {
    client.release();
  }
});

app.post("/api/auth/logout", authenticate, async (req, res) => {
  await pool.query("DELETE FROM sync_sessions WHERE token_hash=$1", [hash(req.token)]);
  res.status(204).end();
});

app.post("/api/sync", authenticate, async (req, res) => {
  const operationId = String(req.body.operationId ?? "");
  const deviceId = String(req.body.deviceId ?? "");
  const incoming = req.body.snapshot;
  if (!isUuid(operationId) || !isUuid(deviceId) || !incoming || typeof incoming !== "object") {
    return res.status(400).json({ error: "Operación de sincronización inválida." });
  }

  const client = await pool.connect();
  try {
    await client.query("BEGIN");
    await client.query("SELECT id FROM sync_users WHERE id=$1 FOR UPDATE", [req.userId]);
    const duplicate = await client.query(
      "SELECT applied_revision,payload FROM sync_operations WHERE user_id=$1 AND operation_id=$2",
      [req.userId, operationId],
    );
    let state = await client.query(
      "SELECT snapshot,revision FROM sync_state WHERE user_id=$1 FOR UPDATE",
      [req.userId],
    );
    if (duplicate.rowCount && canonicalJson(duplicate.rows[0].payload) === canonicalJson(incoming)) {
      await client.query("COMMIT");
      return res.json({ snapshot: state.rows[0]?.snapshot ?? incoming, revision: Number(duplicate.rows[0].applied_revision) });
    }
    const merged = mergeSnapshots(state.rows[0]?.snapshot, incoming);
    const revision = Number(state.rows[0]?.revision ?? 0) + 1;
    await client.query(
      `INSERT INTO sync_state(user_id,snapshot,revision) VALUES($1,$2,$3)
       ON CONFLICT(user_id) DO UPDATE SET snapshot=EXCLUDED.snapshot,revision=EXCLUDED.revision,updated_at=now()`,
      [req.userId, merged, revision],
    );
    await client.query(
      `INSERT INTO sync_operations(user_id,operation_id,device_id,payload,applied_revision) VALUES($1,$2,$3,$4,$5)
       ON CONFLICT(user_id,operation_id) DO UPDATE SET payload=EXCLUDED.payload,applied_revision=EXCLUDED.applied_revision`,
      [req.userId, operationId, deviceId, incoming, revision],
    );
    await client.query("COMMIT");
    res.json({ snapshot: merged, revision });
  } catch (error) {
    await client.query("ROLLBACK");
    throw error;
  } finally {
    client.release();
  }
});

app.use((error, _req, res, _next) => {
  console.error(error);
  res.status(500).json({ error: "No se pudo completar la solicitud." });
});

async function createSession(userId) {
  const token = crypto.randomBytes(32).toString("base64url");
  const expiresAt = new Date(Date.now() + 30 * 24 * 60 * 60 * 1000);
  await pool.query(
    "INSERT INTO sync_sessions(token_hash,user_id,expires_at) VALUES($1,$2,$3)",
    [hash(token), userId, expiresAt],
  );
  return { token, userId, expiresAt: expiresAt.toISOString() };
}

async function sendPasswordResetEmail(email, code) {
  const appUrl = (process.env.PASSWORD_RESET_APP_URL || "https://td-app.replit.app").replace(/\/+$/, "");
  const logoUrl = `${appUrl}/assets/td-coins-email-logo.png`;
  const safeCode = escapeHtml(code);
  const response = await connectors.proxy("resend", "/emails", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: {
      from: process.env.PASSWORD_RESET_FROM || "TD-Coins <onboarding@resend.dev>",
      to: [email],
      subject: "Código para recuperar tu cuenta de TD-Coins",
      text: [
        "TD-Coins",
        "",
        "Recibimos una solicitud para recuperar tu cuenta.",
        `Tu código es: ${code}`,
        "Caduca en 15 minutos y solo puede usarse una vez.",
        "",
        "Abre TD-App para introducirlo. Si no solicitaste este cambio, puedes ignorar este correo.",
      ].join("\n"),
      html: `<!doctype html>
<html lang="es">
  <head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <meta name="color-scheme" content="light" />
    <title>Recupera tu cuenta de TD-Coins</title>
    <style>
      @media only screen and (max-width: 620px) {
        .email-shell { padding: 18px 10px !important; }
        .email-card { border-radius: 18px !important; }
        .email-content { padding: 28px 22px !important; }
        .code-value { font-size: 30px !important; letter-spacing: 7px !important; }
      }
    </style>
  </head>
  <body style="margin:0;background:#f5f0ff;color:#21123b;font-family:Arial,Helvetica,sans-serif;">
    <div style="display:none;max-height:0;overflow:hidden;opacity:0;">
      Tu código de recuperación de TD-Coins caduca en 15 minutos.
    </div>
    <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="background:#f5f0ff;">
      <tr>
        <td class="email-shell" align="center" style="padding:42px 16px;">
          <table role="presentation" class="email-card" width="100%" cellspacing="0" cellpadding="0" border="0" style="max-width:580px;background:#ffffff;border:1px solid #e5dafa;border-radius:24px;overflow:hidden;box-shadow:0 16px 42px rgba(74,40,131,.12);">
            <tr>
              <td style="height:6px;background:linear-gradient(90deg,#7c3aed 0%,#a855f7 54%,#14b8a6 100%);font-size:0;line-height:0;">&nbsp;</td>
            </tr>
            <tr>
              <td class="email-content" style="padding:34px 42px 38px;">
                <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0">
                  <tr>
                    <td align="center">
                      <img src="${logoUrl}" width="76" height="76" alt="Logo de TD-Coins" style="display:block;width:76px;height:76px;border:0;border-radius:22px;" />
                      <div style="padding-top:14px;color:#21123b;font-size:20px;font-weight:700;letter-spacing:-.3px;">TD-Coins</div>
                      <div style="padding-top:5px;color:#786a91;font-size:12px;letter-spacing:1.5px;text-transform:uppercase;">Pequeños pasos, grandes avances</div>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding-top:34px;">
                      <div style="color:#21123b;font-size:27px;line-height:1.18;font-weight:700;letter-spacing:-.5px;">Recupera tu cuenta</div>
                      <div style="padding-top:13px;color:#66577d;font-size:16px;line-height:1.6;">Recibimos una solicitud para cambiar tu contraseña. Abre TD-App y utiliza este código:</div>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding-top:24px;">
                      <table role="presentation" width="100%" cellspacing="0" cellpadding="0" border="0" style="background:#f3edff;border:1px solid #e0d0ff;border-radius:18px;">
                        <tr>
                          <td align="center" style="padding:22px 14px 20px;">
                            <div style="color:#816e9c;font-size:11px;font-weight:700;letter-spacing:1.8px;text-transform:uppercase;">Código de recuperación</div>
                            <div class="code-value" style="padding-top:10px;color:#6d28d9;font-size:36px;line-height:1;font-weight:700;letter-spacing:9px;">${safeCode}</div>
                          </td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding-top:24px;">
                      <table role="presentation" cellspacing="0" cellpadding="0" border="0">
                        <tr>
                          <td valign="top" style="padding:2px 11px 0 0;color:#14a896;font-size:17px;">●</td>
                          <td style="color:#66577d;font-size:14px;line-height:1.55;">Caduca en <strong style="color:#33204f;">15 minutos</strong> y solo puede utilizarse una vez.</td>
                        </tr>
                        <tr>
                          <td valign="top" style="padding:10px 11px 0 0;color:#14a896;font-size:17px;">●</td>
                          <td style="padding-top:8px;color:#66577d;font-size:14px;line-height:1.55;">Si no solicitaste este cambio, ignora el correo. Tu cuenta seguirá protegida.</td>
                        </tr>
                      </table>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding-top:32px;border-top:1px solid #eee8f8;">
                      <div style="color:#8a7b9f;font-size:12px;line-height:1.6;">Este mensaje fue enviado automáticamente. No compartas tu código con nadie.</div>
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
          <div style="max-width:520px;padding:22px 12px 0;color:#988eaa;font-size:12px;line-height:1.5;text-align:center;">TD-Coins · Enfócate en lo que importa, un paso a la vez.</div>
        </td>
      </tr>
    </table>
  </body>
</html>`,
    },
  });
  if (!response.ok) {
    throw new Error(`Resend returned ${response.status}: ${await response.text()}`);
  }
}

function escapeHtml(value) {
  return String(value).replace(/[&<>"']/g, (character) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;",
  }[character]));
}

async function consumeResetLimit(client, scope, maximum) {
  const result = await client.query(
    `INSERT INTO password_reset_limits(scope_hash,window_start,request_count)
     VALUES($1,now(),1)
     ON CONFLICT(scope_hash) DO UPDATE SET
       window_start=CASE
         WHEN password_reset_limits.window_start<=now()-interval '15 minutes' THEN now()
         ELSE password_reset_limits.window_start
       END,
       request_count=CASE
         WHEN password_reset_limits.window_start<=now()-interval '15 minutes' THEN 1
         ELSE password_reset_limits.request_count+1
       END
     RETURNING request_count`,
    [hash(scope)],
  );
  return result.rows[0].request_count <= maximum;
}

async function authenticate(req, res, next) {
  const token = req.get("authorization")?.replace(/^Bearer\s+/i, "");
  if (!token) return res.status(401).json({ error: "Inicia sesión para sincronizar." });
  const result = await pool.query(
    "SELECT user_id FROM sync_sessions WHERE token_hash=$1 AND expires_at>now()",
    [hash(token)],
  );
  if (!result.rowCount) return res.status(401).json({ error: "La sesión ha caducado." });
  req.token = token;
  req.userId = result.rows[0].user_id;
  next();
}

const isUuid = (value) => /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i.test(value);
const canonicalJson = (value) => JSON.stringify(value, (_key, nested) => {
  if (!nested || Array.isArray(nested) || typeof nested !== "object") return nested;
  return Object.fromEntries(Object.keys(nested).sort().map((key) => [key, nested[key]]));
});
const port = Number(process.env.PORT || 3000);
if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  app.listen(port, "0.0.0.0", () => console.log(`Sync API listening on ${port}`));
}

export { app, pool };