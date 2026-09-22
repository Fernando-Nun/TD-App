---
name: Android build environment
description: Reanudación de compilaciones Android cuando el SDK local no está disponible.
---

La compilación del APK requiere que el SDK Android exista en la ruta indicada por `local.properties` o por `ANDROID_HOME`/`ANDROID_SDK_ROOT`; si esa ruta no existe, Gradle falla antes de compilar el código. En este entorno, el SDK mínimo reproducible se obtiene con `androidenv.composeAndroidPackages`, API 35 y Build-Tools 34/36, aceptando la licencia solo durante la construcción local.

**Why:** En esta sesión Gradle no pudo iniciar la compilación porque la ruta configurada al SDK no estaba presente en el contenedor.

**How to apply:** Antes de atribuir un error a Kotlin o Compose, comprobar primero que la ruta del SDK configurada existe y contiene las herramientas de Android; si falta, usar una composición Nix mínima, aceptar `NIXPKGS_ACCEPT_ANDROID_SDK_LICENSE=1` solo en ese comando y apuntar `local.properties` al directorio interno `libexec/android-sdk`. Si aparecen muchos símbolos existentes como no resueltos tras cambios válidos, detener daemons y usar `./gradlew --no-daemon clean testDebugUnitTest`.