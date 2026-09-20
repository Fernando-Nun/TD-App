#!/usr/bin/env bash
set -euo pipefail

echo "Installing backend dependencies..."
npm ci --no-audit --no-fund

if [[ -z "${DATABASE_URL:-}" ]]; then
  echo "DATABASE_URL is not available; backend migration cannot run." >&2
  exit 1
fi

echo "Applying the idempotent sync schema..."
npm run db:migrate

echo "Post-merge setup completed."