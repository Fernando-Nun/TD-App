---
name: Android build environment
description: Reanudación de compilaciones Android cuando el SDK local no está disponible.
---

La compilación del APK requiere que el SDK Android exista en la ruta indicada por `local.properties` o por `ANDROID_HOME`/`ANDROID_SDK_ROOT`; si esa ruta no existe, Gradle falla antes de compilar el código. En este entorno, el SDK mínimo reproducible se obtiene con `androidenv.composeAndroidPackages`, API 35 y Build-Tools 34/36, aceptando la licencia solo durante la construcción local. Las rutas temporales bajo `/tmp` pueden desaparecer entre sesiones; conviene validar y usar la composición Android disponible en `/nix/store`.

Con Android API 35, la transformación `androidJdkImage`/`jlink` falló al ejecutar Gradle bajo el GraalVM JDK 19 predeterminado, aunque el SDK estaba presente. Usar un OpenJDK 17 ya instalado hizo que compilación, pruebas unitarias y compilación de pruebas instrumentadas terminaran correctamente.

**Why:** Los errores del toolchain pueden parecer problemas del SDK o del código aunque la causa sea la combinación de JDK y `jlink`; además, las rutas temporales configuradas para el SDK pueden desaparecer entre sesiones.

**How to apply:** Antes de atribuir un error a Kotlin o Compose, comprobar primero que la ruta del SDK configurada existe y contiene las herramientas de Android. Si `androidJdkImage` o `jlink` falla con el JDK predeterminado, ejecutar Gradle con un OpenJDK 17 disponible mediante `JAVA_HOME` y `PATH`. Si falta el SDK, usar una composición Nix mínima, aceptar `NIXPKGS_ACCEPT_ANDROID_SDK_LICENSE=1` solo en ese comando y apuntar `local.properties` al directorio interno `libexec/android-sdk`. Si aparecen muchos símbolos existentes como no resueltos tras cambios válidos, detener daemons y usar `./gradlew --no-daemon clean testDebugUnitTest`.