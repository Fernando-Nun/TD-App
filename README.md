# TD-Coins para Android

Aplicación nativa para Android, migrada completamente a **Kotlin + Jetpack Compose**. Conserva la experiencia del prototipo original: Inicio, Pomodoro, Misiones, Tienda y un plan personalizado de retos.

## Requisitos

- Android Studio Ladybug o una versión más reciente
- JDK 17
- Android SDK 35
- Dispositivo o emulador con Android 8.0 (API 26) o superior

## Abrir en Android Studio

1. Descarga o clona este proyecto.
2. En Android Studio selecciona **Open**.
3. Selecciona la carpeta raíz `TDCoins`, donde se encuentra `settings.gradle.kts`.
4. Espera a que Android Studio termine la sincronización de Gradle.
5. Selecciona la configuración **app** y ejecuta en un emulador o dispositivo.

## Compilar desde terminal

```bash
./gradlew assembleDebug
```

El APK se genera en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Tecnología

- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- SpeechRecognizer de Android para transcripción
- SharedPreferences para modo sin conexión + API PostgreSQL para sincronización

## Funciones implementadas

- Misiones personalizadas con progreso y recompensas
- Pomodoro con persistencia de estadísticas
- Rachas diarias y notificaciones de progreso
- Dictado de notas, edición y conversión directa a misión
- Respaldo/restauración del estado mediante los servicios de copia de Android
- Inicio de sesión y sincronización entre dispositivos con reconciliación sin duplicar recompensas
- Navegación adaptable para teléfonos y tabletas
- Etiquetas y semántica para tecnologías de asistencia

## Documentación y pruebas

- [Proceso y decisiones de desarrollo](docs/DEVELOPMENT.md)
- [Plan de pruebas](docs/TEST_PLAN.md)

Ejecutar pruebas y generar APK:

```bash
./gradlew testDebugUnitTest assembleDebug
```

## Servidor de sincronización

El servicio se ejecuta con `npm start` y requiere `DATABASE_URL` y
`SESSION_SECRET` en secretos. Para compilar la app contra un servidor publicado:

```bash
SYNC_API_URL=https://tu-servidor.example ./gradlew assembleDebug
```

No se incluyen credenciales en el repositorio.

El esquema de desarrollo está versionado en `server/schema.sql`. En Replit, el
esquema ya aplicado a la base de desarrollo se propaga a producción mediante
Publish. Para preparar una base de desarrollo vacía se puede ejecutar
`npm run db:migrate`.