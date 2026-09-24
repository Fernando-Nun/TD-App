---
name: Fallback WebGL
description: Restricción del preview y criterio para escenas 3D en la web de TD-App
---

Las experiencias 3D de la web deben conservar una alternativa visual funcional cuando el navegador no puede crear un contexto WebGL.

**Why:** El preview de Replit puede ejecutarse sin aceleración WebGL; Three.js registra un error y deja la escena vacía si no se comprueba esa capacidad antes de crear el renderer.

**How to apply:** Antes de inicializar una escena WebGL, comprobar la disponibilidad del contexto y mostrar una representación CSS o estática que conserve el contenido y la narrativa. Mantener esa alternativa compatible con movimiento reducido.