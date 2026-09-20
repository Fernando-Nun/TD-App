import crypto from "node:crypto";
import express from "express";
import pg from "pg";
import { mergeSnapshots } from "./merge.js";

const { Pool } = pg;
const app = express();
const pool = new Pool({ connectionString: process.env.DATABASE_URL });
const secret = process.env.SESSION_SECRET;
if (!secret) throw new Error("SESSION_SECRET is required");

app.use(express.json({ limit: "1mb" }));

const hash = (value) => crypto.createHmac("sha256", secret).update(value).digest("hex");
const passwordHash = (password, salt) =>
  crypto.scryptSync(password, salt, 64).toString("hex");

app.get("/health", (_req, res) => res.json({ ok: true }));

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
app.listen(port, "0.0.0.0", () => console.log(`Sync API listening on ${port}`));