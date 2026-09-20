CREATE TABLE IF NOT EXISTS sync_users (
  id uuid PRIMARY KEY,
  email text UNIQUE NOT NULL,
  password_hash text NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS sync_sessions (
  token_hash text PRIMARY KEY,
  user_id uuid NOT NULL REFERENCES sync_users(id) ON DELETE CASCADE,
  expires_at timestamptz NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS password_reset_codes (
  code_hash text PRIMARY KEY,
  user_id uuid NOT NULL REFERENCES sync_users(id) ON DELETE CASCADE,
  expires_at timestamptz NOT NULL,
  used_at timestamptz,
  created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS password_reset_codes_user_id_idx
  ON password_reset_codes(user_id);

CREATE TABLE IF NOT EXISTS password_reset_limits (
  scope_hash text PRIMARY KEY,
  window_start timestamptz NOT NULL DEFAULT now(),
  request_count integer NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS sync_state (
  user_id uuid PRIMARY KEY REFERENCES sync_users(id) ON DELETE CASCADE,
  snapshot jsonb NOT NULL,
  revision bigint NOT NULL DEFAULT 0,
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS sync_operations (
  user_id uuid NOT NULL REFERENCES sync_users(id) ON DELETE CASCADE,
  operation_id uuid NOT NULL,
  device_id uuid NOT NULL,
  payload jsonb NOT NULL,
  applied_revision bigint NOT NULL,
  created_at timestamptz NOT NULL DEFAULT now(),
  PRIMARY KEY(user_id, operation_id)
);