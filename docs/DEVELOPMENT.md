# Desarrollo de TD-Coins

## Objetivo

TD-Coins es una aplicación Android nativa para apoyar enfoque, hábitos y organización mediante misiones, Pomodoros, recompensas y notas de voz. La interfaz está desarrollada completamente en Kotlin con Jetpack Compose.

Para el mapa completo de Compose, la persistencia, la API, Resend y la presentación al cliente, consulta [Arquitectura y presentación de TD-App](ARQUITECTURA_Y_PRESENTACION_TD_APP.md).

## Arquitectura actual

- `App.kt` coordina navegación, estado compartido, guardado local y sincronización.
- `AppPersistence.kt` serializa el snapshot en `SharedPreferences`; `SyncClient.kt` lo sincroniza con la API autenticada.
- La API guarda cuentas y snapshots en PostgreSQL; el detalle está en la guía de arquitectura enlazada arriba.
- `ProgressLogic.kt` contiene reglas de rachas independientes de la interfaz.
- La mayoría de pantallas Compose reciben estado y callbacks; algunas integran API Android, como el reconocimiento de voz y los permisos.
- Los recordatorios se guardan aparte y se programan localmente; no forman parte del snapshot sincronizado.

## Decisiones de diseño

### Datos y nube

La app guarda primero un snapshot local. Con una sesión activa, `SyncClient` envía ese snapshot a la API definida por `SYNC_API_URL` —por defecto `https://td-app.replit.app`— y vuelve a intentarlo periódicamente. El backend autentica la cuenta y guarda el snapshot por usuario en PostgreSQL. Es sincronización eventual de estado, no una garantía de trabajo en segundo plano ni un historial de cada cambio.

### Voz y privacidad

La app solicita permiso de micrófono al iniciar una transcripción. El servicio `SpeechRecognizer` disponible en Android realiza el reconocimiento; la app conserva texto, no un historial de audio. La disponibilidad sin conexión depende del motor instalado. Si se solicita un plan personalizado, el texto del reto sí se envía al backend y al proveedor de IA.

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

La app no conserva un historial de audio. Las notas de texto se guardan localmente y forman parte del snapshot sincronizado tras iniciar sesión; el texto enviado para generar un plan viaja al backend. El acceso a la API requiere sesión para sincronizar y para generar planes. Los recordatorios permanecen en el dispositivo y no se sincronizan.

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