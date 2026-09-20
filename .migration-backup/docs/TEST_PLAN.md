# Plan de pruebas

## Pruebas automatizadas

`ProgressLogicTest` verifica:

1. Inicio de una racha.
2. Varias actividades durante el mismo día.
3. Incremento en días consecutivos.
4. Reinicio después de un día perdido.
5. Recuperación segura ante una fecha guardada inválida.

Ejecutar con:

```bash
./gradlew testDebugUnitTest
```

Las pruebas instrumentadas de `app/src/androidTest` verifican:

1. Creación, avance, finalización, recompensa única y persistencia de misiones.
   Un recorrido abre `MainActivity`, navega a Misiones, crea y completa una misión, reinicia la
   actividad y confirma que el estado se restauró.
2. Permiso de micrófono concedido y denegado, incluida la alternativa de escritura manual.
3. Etiquetas de TalkBack, estado seleccionado, progreso semántico y disponibilidad de controles.
4. Escala de texto al 200 %.
5. Navegación equivalente en ancho de teléfono (360 dp) y tableta (800 dp).

Ejecutar en un emulador o dispositivo desbloqueado con:

```bash
./gradlew connectedDebugAndroidTest
```

La prueba de denegación interactúa con el diálogo del sistema. Antes de ejecutarla, el dispositivo
debe estar desbloqueado y sin otros diálogos superpuestos.

## Pruebas manuales funcionales

### Persistencia y respaldo

1. Crear una misión, completar progreso y comprar un artículo.
2. Cerrar completamente la app y abrirla de nuevo.
3. Confirmar que monedas, misiones, compras, racha y notas siguen presentes.
4. En un dispositivo con copias de seguridad activas, reinstalar durante una prueba controlada y confirmar la restauración.

### Voz

1. Conceder permiso de micrófono.
2. Dictar una nota en español y revisar la transcripción.
3. Editar y guardar el texto.
4. Convertirlo en misión y comprobar que aparece en Misiones.
5. Repetir sin red, sin permiso y sin servicio de reconocimiento disponible.

### Notificaciones y rachas

1. Conceder permiso de notificaciones en Android 13 o superior.
2. Completar una misión y un Pomodoro.
3. Verificar el mensaje, recompensa y racha sin duplicación en el mismo día.

### Accesibilidad

1. Recorrer todas las pantallas con TalkBack.
2. Activar texto al 200 % y tamaño de pantalla grande.
3. Verificar contraste, etiquetas, orden de foco y áreas táctiles.
4. Probar teléfono pequeño, teléfono grande, tableta y orientación horizontal.

## Criterios de aceptación

- No hay cierres inesperados en los flujos anteriores.
- El progreso no se pierde al reiniciar.
- Denegar permisos mantiene una alternativa manual.
- La navegación sigue disponible con texto grande y en distintos anchos.
- El APK debug se instala en Android 8.0 (API 26) o posterior.

## Matriz de dispositivos y resultados

| Dispositivo | Android | Ancho / configuración | Resultado |
| --- | --- | --- | --- |
| Emulador Pixel 4 | API 35 | 360 dp, texto 100 % y 200 % | Pendiente de ejecución en Android Studio |
| Emulador Pixel Tablet | API 35 | 800 dp, texto 100 % y 200 % | Pendiente de ejecución en Android Studio |
| Dispositivo físico mínimo | API 26 o superior | TalkBack, orientación vertical y horizontal | Pendiente de dispositivo |

En Replit se validaron `testDebugUnitTest`, la compilación de todas las fuentes instrumentadas y la
generación de `app-debug-androidTest.apk`. No había ningún dispositivo conectado y la instalación
del emulador del SDK terminó de forma anormal antes de crear el AVD. Los resultados instrumentados
solo se marcan como aprobados después de ejecutar `connectedDebugAndroidTest` en los dispositivos
indicados; la tabla no presenta como realizadas pruebas que este entorno no pudo ejecutar.