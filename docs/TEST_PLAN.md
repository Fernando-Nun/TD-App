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