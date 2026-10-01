# TD-App para Android

## Video tutorial
[Ver cómo se usa TD-App en YouTube](https://youtu.be/GG30z0a6hUI)

## Descargar la aplicación
[Visitar la página de descarga de TD-App](https://td-app.replit.app/) · [Descargar APK directamente](https://td-app.replit.app/downloads/td-app.apk?v=1.9)

TD-App es una aplicación Android de apoyo para organizar tareas y enfoque mediante misiones, Pomodoro y recordatorios. No diagnostica ni trata el TDAH.

## Funciones principales
- Temporizador Pomodoro y registro de sesiones.
- Misiones personalizadas con seguimiento del progreso.
- Rachas y recordatorios.
- Notas por voz y conversión de notas en misiones.
- Recompensas virtuales con TD-Coins.
- Inicio de sesión y sincronización entre dispositivos.
- Navegación adaptable y etiquetas para tecnologías de asistencia.

## Tecnología
Kotlin, Jetpack Compose, Material 3, SharedPreferences para el guardado local y una API con PostgreSQL para la sincronización.

## Requisitos
- Android Studio Ladybug o posterior.
- JDK 17 y Android SDK 35.
- Android 8.0 (API 26) o posterior.

## Ejecutar y compilar
Abre la carpeta del proyecto en Android Studio y ejecuta la configuración `app`.

Para ejecutar las pruebas unitarias y compilar:

```bash
./gradlew testDebugUnitTest assembleDebug
```

El APK de prueba se genera en `app/build/outputs/apk/debug/app-debug.apk`.

## Servidor de sincronización
El servidor requiere las variables de entorno `DATABASE_URL` y `SESSION_SECRET`. Para compilar la app contra otro servidor, configura `SYNC_API_URL`. No incluyas credenciales en el repositorio.