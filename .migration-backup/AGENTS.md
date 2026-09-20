# TD-Coins Android

Aplicación Android nativa escrita en Kotlin con Jetpack Compose.

## Estructura

- `app/src/main/java/com/tdcoins/app/` — actividad, tema, modelos, datos y pantallas Compose.
- `app/src/main/res/drawable/` — logo e imágenes del catálogo.
- `app/src/main/AndroidManifest.xml` — manifiesto Android.
- `app/build.gradle.kts` — configuración del módulo Android.
- `build.gradle.kts` y `settings.gradle.kts` — configuración Gradle del proyecto.

## Desarrollo

- Abrir la carpeta raíz con Android Studio.
- Esperar a que termine la sincronización de Gradle.
- Ejecutar la configuración `app` en un emulador o dispositivo Android 8.0 o superior.
- Compilación por terminal: `./gradlew assembleDebug`.

## Convenciones

- Implementar interfaz y lógica en Kotlin.
- Usar Jetpack Compose para todas las pantallas; no crear layouts XML.
- Mantener textos visibles en español y reutilizar el tema definido en `Theme.kt`.
- Mantener el estado compartido de monedas, Pomodoros y misiones en `App.kt`.
