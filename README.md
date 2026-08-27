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

No utiliza React, TypeScript, Java ni layouts XML para la interfaz.