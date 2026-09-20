---
name: Android build environment
description: Reanudación de compilaciones Android cuando el SDK local no está disponible.
---

La compilación del APK requiere que el SDK Android exista en la ruta indicada por `local.properties` o por `ANDROID_HOME`/`ANDROID_SDK_ROOT`; si esa ruta no existe, Gradle falla antes de compilar el código.

**Why:** En esta sesión Gradle no pudo iniciar la compilación porque la ruta configurada al SDK no estaba presente en el contenedor.

**How to apply:** Antes de atribuir un error a Kotlin o Compose, comprobar primero que la ruta del SDK configurada existe y contiene las herramientas de Android.