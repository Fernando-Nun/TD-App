# Desarrollo de TD-Coins

## Objetivo

TD-Coins es una aplicación Android nativa para apoyar enfoque, hábitos y organización mediante misiones, Pomodoros, recompensas y notas de voz. La interfaz está desarrollada completamente en Kotlin con Jetpack Compose.

## Arquitectura actual

- `App.kt` coordina navegación y estado compartido.
- `AppPersistence.kt` serializa el estado en `SharedPreferences`.
- Android Auto Backup incluye ese estado en la copia de seguridad de la cuenta del dispositivo y permite restaurarlo al reinstalar o transferir la aplicación.
- `ProgressLogic.kt` contiene reglas de rachas independientes de la interfaz.
- Las pantallas Compose reciben estado y callbacks; no acceden directamente al almacenamiento.
- `SpeechRecognizer` proporciona dictado en español sin almacenar audio en la app.

## Decisiones de diseño

### Datos y nube

Se eligió Android Auto Backup como primera integración en la nube porque no requiere credenciales privadas, conserva la instalación nativa y permite respaldar el progreso. Esta versión no ofrece sincronización en tiempo real entre usuarios. Una fase futura puede implementar un repositorio remoto con autenticación y una base de datos administrada sin cambiar los modelos de pantalla.

### Voz y privacidad

La app solicita permiso de micrófono solamente al iniciar una transcripción. El servicio de reconocimiento disponible en el dispositivo procesa el dictado; TD-Coins conserva únicamente el texto que el usuario decide guardar. La disponibilidad sin conexión depende del motor instalado.

### Progreso y rachas

Completar una misión o un Pomodoro registra actividad del día. Varias acciones el mismo día no aumentan artificialmente la racha; un día consecutivo la incrementa y una interrupción la reinicia.

### Accesibilidad y adaptación

- Los controles principales tienen etiquetas para lector de pantalla.
- El progreso de las misiones se expone como información semántica.
- Los controles táctiles principales tienen dimensiones adecuadas.
- Las categorías se desplazan horizontalmente con texto grande.
- En pantallas desde 700 dp se usa navegación lateral; en teléfonos se mantiene navegación inferior.
- Los textos usan unidades `sp` y respetan la escala configurada en Android.

## Privacidad

No se guarda audio ni se envía a un servidor propio. El usuario puede escribir una nota manualmente si no concede acceso al micrófono. La copia de seguridad depende de la configuración de respaldo de Android del usuario.

## Compilación

Requisitos:

- JDK 17 o posterior compatible con Gradle
- Android SDK 35
- Android Build Tools

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

El APK se genera en `app/build/outputs/apk/debug/app-debug.apk`.