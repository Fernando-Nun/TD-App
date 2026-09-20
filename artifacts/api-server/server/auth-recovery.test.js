import assert from "node:assert/strict";
import crypto from "node:crypto";
import { after, before, test } from "node:test";
import { app, pool } from "./index.js";

let server;
let baseUrl;
const createdEmails = [];
const deliveredCodes = new Map();

before(async () => {
  server = app.listen(0, "127.0.0.1");
  await new Promise((resolve) => server.once("listening", resolve));
  baseUrl = `http://127.0.0.1:${server.address().port}`;
  app.locals.sendPasswordResetEmail = async (email, code) => deliveredCodes.set(email, code);
});

after(async () => {
  await pool.query("DELETE FROM sync_users WHERE email=ANY($1)", [createdEmails]);
  await pool.query("DELETE FROM password_reset_limits WHERE scope_hash=ANY($1)", [
    [...createdEmails.map((email) => keyedHash(`account:${email}`)), keyedHash("source:127.0.0.1")],
  ]);
  await new Promise((resolve, reject) => server.close((error) => error ? reject(error) : resolve()));
  await pool.end();
});

test("password recovery is non-enumerating, expiring, one-time, and revokes sessions", async () => {
  const email = uniqueEmail("flow");
  const missingEmail = uniqueEmail("missing");
  const oldPassword = "OldPassword123!";
  const newPassword = "NewPassword456!";
  const registration = await post("/api/auth/register", { email, password: oldPassword });
  assert.equal(registration.status, 201);

  const knownRequest = await post("/api/auth/password-reset/request", { email });
  const missingRequest = await post("/api/auth/password-reset/request", { email: missingEmail });
  assert.equal(knownRequest.status, 202);
  assert.deepEqual(knownRequest.body, missingRequest.body);

  const code = deliveredCodes.get(email);
  assert.ok(code);
  const confirmation = await post("/api/auth/password-reset/confirm", {
    email, code, password: newPassword,
  });
  assert.equal(confirmation.status, 200);

  const reused = await post("/api/auth/password-reset/confirm", {
    email, code, password: oldPassword,
  });
  assert.equal(reused.status, 400);

  const oldSession = await post("/api/sync", {}, registration.body.token);
  assert.equal(oldSession.status, 401);
  assert.equal((await post("/api/auth/login", { email, password: newPassword })).status, 200);

  const expiredCode = "EXPIRED-CODE";
  const user = await pool.query("SELECT id FROM sync_users WHERE email=$1", [email]);
  await pool.query(
    `INSERT INTO password_reset_codes(code_hash,user_id,expires_at)
     VALUES($1,$2,now()-interval '1 second')`,
    [keyedHash(expiredCode), user.rows[0].id],
  );
  const expired = await post("/api/auth/password-reset/confirm", {
    email, code: expiredCode, password: oldPassword,
  });
  assert.equal(expired.status, 400);
});

test("only one concurrent confirmation can consume a code", async () => {
  const email = uniqueEmail("concurrent");
  await post("/api/auth/register", { email, password: "InitialPassword!" });
  await post("/api/auth/password-reset/request", { email });
  const code = deliveredCodes.get(email);
  const results = await Promise.all([
    post("/api/auth/password-reset/confirm", { email, code, password: "FirstPassword123!" }),
    post("/api/auth/password-reset/confirm", { email, code, password: "SecondPassword123!" }),
  ]);
  assert.deepEqual(results.map(({ status }) => status).sort(), [200, 400]);
});

test("reset requests are limited per account without changing the public response", async () => {
  const email = uniqueEmail("limited");
  await post("/api/auth/register", { email, password: "InitialPassword!" });
  const responses = [];
  for (let index = 0; index < 5; index += 1) {
    deliveredCodes.delete(email);
    const response = await post("/api/auth/password-reset/request", { email });
    responses.push({ response, delivered: deliveredCodes.has(email) });
  }
  assert.ok(responses.every(({ response }) => response.status === 202));
  assert.ok(responses.every(({ response }) =>
    JSON.stringify(response.body) === JSON.stringify(responses[0].response.body)));
  assert.equal(responses.filter(({ delivered }) => delivered).length, 3);
});

function uniqueEmail(prefix) {
  const email = `${prefix}-${crypto.randomUUID()}@example.com`;
  createdEmails.push(email);
  return email;
}

function keyedHash(value) {
  return crypto.createHmac("sha256", process.env.SESSION_SECRET).update(value).digest("hex");
}

async function post(path, body, token) {
  const response = await fetch(`${baseUrl}${path}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: JSON.stringify(body),
  });
  return {
    status: response.status,
    body: await response.json(),
  };
}