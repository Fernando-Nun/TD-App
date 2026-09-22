---
name: Gemini model availability
description: Compatibilidad variable entre la lista de modelos de Gemini y los modelos que una clave nueva puede generar.
---

Las claves nuevas de Gemini pueden devolver modelos antiguos en `models.list` aunque `generateContent` los rechace por estar retirados o no disponibles. La generación debe preferir un modelo Flash vigente y reintentar con otros modelos compatibles cuando Google responda 404, 429 o 503.

**Why:** La primera selección de `gemini-2.5-flash` produjo 404 y el modelo recomendado tuvo respuestas 503 por alta demanda; usar una sola opción hacía que todos los retos terminaran en el mismo fallback.

**How to apply:** Consultar los modelos con `generateContent`, excluir modelos retirados conocidos, priorizar el modelo Flash ligero y conservar un fallback que incluya el texto exacto del objetivo.