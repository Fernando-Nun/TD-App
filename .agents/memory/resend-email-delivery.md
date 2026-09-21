---
name: Envío con Resend
description: Configuración resiliente del envío transaccional cuando el conector de Replit no queda vinculado al runtime.
---

El envío transaccional debe conservar un camino seguro con `RESEND_API_KEY` además del conector de Replit. Una cuenta o dominio activo en Resend no implica que el conector esté vinculado al runtime del proyecto.

**Why:** el runtime puede devolver `No connection found ... connector: resend` aunque la cuenta de Resend o su dominio aparezcan activos en otra interfaz.

**How to apply:** guardar la clave únicamente como secreto del proyecto, mantener un remitente verificado en `PASSWORD_RESET_FROM` y publicar/reiniciar el backend después de cualquier cambio.