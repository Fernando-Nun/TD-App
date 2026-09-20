# TD-Coins

Aplicación Android nativa para enfoque, hábitos y organización, acompañada por un backend de autenticación y sincronización.

## Run & Operate

- `pnpm --filter @workspace/api-server run dev` — migra la base, compila y ejecuta el backend mediante el workflow administrado
- `pnpm --filter @workspace/api-server run test:server` — pruebas del backend
- `pnpm --filter @workspace/api-server run db:migrate` — aplica el esquema PostgreSQL idempotente
- `./gradlew testDebugUnitTest assembleDebug` — valida Android y genera el APK
- Variables requeridas: `DATABASE_URL` y `SESSION_SECRET`
- Variable opcional: `PASSWORD_RESET_FROM`

## Stack

- Android: Kotlin, Jetpack Compose, Gradle Kotlin DSL
- API: Express 5 sobre Node.js
- Datos: PostgreSQL con SQL idempotente conservado
- Correo: conector Resend para recuperación de contraseña
- Workspace: pnpm y artefactos administrados por Replit
- Build del backend: esbuild ESM

## Where things live

- `app/`, `gradle/`, `build.gradle.kts`, `settings.gradle.kts` — proyecto Android nativo completo
- `artifacts/api-server/server/` — rutas Express, reconciliación, migración SQL y pruebas originales
- `artifacts/api-server/server/schema.sql` — esquema de sincronización y recuperación
- `docs/` — decisiones de desarrollo y plan de pruebas Android

## Architecture decisions

- Android permanece en la raíz para conservar sin cambios la estructura que abre Android Studio.
- El backend vive en el artefacto API, pero mantiene sus rutas y comportamiento JavaScript original.
- El proxy del artefacto publica `/api/*`; `/health` se conserva y `/api/healthz` sirve al health check administrado.
- La migración SQL se ejecuta antes de iniciar el backend y después de cada fusión.

## Product

TD-Coins ofrece misiones, Pomodoro, recompensas, dictado, accesibilidad, recordatorios, cuentas, recuperación de contraseña y sincronización entre dispositivos.

## User preferences

Mantener la aplicación Android 100% Kotlin y Jetpack Compose; no migrarla a web o Expo.

## Gotchas

- La interfaz Android no tiene preview web en Replit; se valida con Gradle y en dispositivo/emulador.
- `SYNC_API_URL` se inyecta al compilar Android; si no se define usa `REPLIT_DEV_DOMAIN`.
- No ejecutar `pnpm dev` en la raíz; usar el workflow administrado del artefacto.

## Pointers

- See the `pnpm-workspace` skill for workspace structure, TypeScript setup, and package details
