#!/usr/bin/env python3
"""Build the branded TD-App Word guide using only Python's standard library."""

from __future__ import annotations

import base64
import html as html_lib
import re
import shutil
import struct
import subprocess
import tempfile
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET


ROOT = Path(__file__).resolve().parents[1]
LOGO = ROOT / "app" / "src" / "main" / "res" / "drawable" / "logo.png"
OUTPUT = ROOT / "Documentación" / "Documentacion_TD-App.docx"
PDF_OUTPUT = ROOT / "Documentación" / "Documentacion_TD-App.pdf"
SCREENSHOTS = [
    (
        ROOT / "attached_assets" / "0_imagen_2026-09-28_172137287_1790637697292.png",
        "Inicio: resumen del saldo, el progreso y los accesos a las funciones principales.",
    ),
    (
        ROOT / "attached_assets" / "0_imagen_2026-09-28_172208905_1790637728909.png",
        "Pomodoro: temporizador de enfoque, controles de la sesión y consejo para el bloque.",
    ),
    (
        ROOT / "attached_assets" / "0_imagen_2026-09-28_172246848_1790637766853.png",
        "Misiones: seguimiento visual del avance por pasos y categorías.",
    ),
    (
        ROOT / "attached_assets" / "0_imagen_2026-09-28_172323502_1790637803506.png",
        "Tienda interna: catálogo visual de recompensas para canje con TD-Coins.",
    ),
    (
        ROOT / "attached_assets" / "0_imagen_2026-09-28_172812503_1790638092508.png",
        "Mi perfil: dictado o entrada manual, notas y retos principales.",
    ),
    (
        ROOT / "attached_assets" / "0_imagen_2026-09-28_172922722_1790638162726.png",
        "Plan personalizado: recordatorios sugeridos y pasos de acción para un desafío.",
    ),
    (
        ROOT / "screenshots" / "td-app-landing-live.png",
        "Sitio público: presentación del producto y acceso a la descarga de la aplicación.",
    ),
]

GUIDE_MARKDOWN = """# TD-App: resumen del proyecto

## Ficha del proyecto

| Dato | Información |
|---|---|
| Nombre | TD-App |
| Equipo | Pingüino |
| Integrantes | Luis Fernando Núñez Díaz; Ricardo Baranda Cisneros; Bryan David Mariñelarena Ponce |
| Producto | Aplicación Android nativa y sitio público informativo |
| Fecha de elaboración | Septiembre de 2026 |

## Descripción, problema y público

TD-App es una herramienta de enfoque, hábitos y organización. Ayuda a convertir objetivos en acciones pequeñas, iniciar periodos de concentración y ver el avance mediante misiones, rachas y recompensas. El sitio público explica la propuesta y permite acceder a la descarga de Android.

El problema que aborda es la dificultad para organizar tareas, comenzar una actividad y sostener la atención o un hábito. El público al que se dirige incluye personas que buscan apoyo para planificar sus actividades, en especial quienes experimentan retos de atención u organización, como algunas personas con TDAH. La aplicación es una herramienta de acompañamiento y no diagnostica ni sustituye atención profesional.

## Objetivo y funciones

La propuesta busca que el usuario pase de una intención general a un siguiente paso visible, con retroalimentación sencilla sobre su avance.

| Función | Uso |
|---|---|
| Inicio | Consulta saldo TD-Coins, Pomodoros, misiones, racha, frase del día y accesos rápidos. |
| Pomodoro | Inicia, pausa o reinicia bloques de enfoque; registra sesiones y ofrece descansos. |
| Misiones | Crea objetivos por categoría y meta de pasos; permite avanzar y recibir recompensas al completar. |
| Retos y planes | Describe un desafío por texto o voz y solicita un plan con sugerencias y acciones concretas. |
| Notas | Guarda texto reconocido o escrito y permite convertir una nota en una misión. El audio original no se mantiene como historial. |
| Recordatorios | Configura avisos locales para enfoque, misiones o rachas, además de horarios silenciosos. |
| Tienda interna | Presenta recompensas visuales y permite canjearlas con TD-Coins dentro de la aplicación; no procesa pagos ni pedidos en línea. |
| Sitio público | Presenta cómo funciona, funciones, recompensas, preguntas frecuentes y descarga del APK. |

## Experiencia de uso y decisiones de diseño

La aplicación organiza sus funciones en cinco secciones principales con navegación persistente. La pantalla de Inicio concentra el estado general; las demás se enfocan en una tarea concreta. Las tarjetas, indicadores de progreso, categorías con color y textos de acción hacen visible qué puede hacer el usuario y qué avance ha realizado. La interfaz usa fondo claro y acentos morados, turquesa y dorados.

El sitio web acompaña a la aplicación, pero no es una tienda electrónica: sus recompensas son una presentación visual y sus botones llevan a la descarga. El producto combina una app nativa con un sitio responsivo para que la explicación del proyecto pueda consultarse en pantallas de distintos tamaños.

## Accesibilidad y adaptación

La aplicación incluye etiquetas descriptivas para controles, estados accesibles para selecciones, información semántica del progreso y orden de lectura en pantallas relevantes. La navegación cambia entre barra inferior y navegación lateral según el ancho de pantalla. Se han contemplado el aumento del tamaño de texto y el desplazamiento del contenido; estas decisiones no equivalen a una certificación universal de accesibilidad.

El dictado solicita permiso de micrófono, distingue errores habituales del servicio y conserva la escritura manual como alternativa. En el sitio web hay etiquetas para controles, textos alternativos para imágenes, una alternativa descriptiva al teléfono 3D cuando WebGL no está disponible y una preferencia para reducir movimiento.

## Tecnologías y arquitectura

| Parte | Tecnologías y función |
|---|---|
| Aplicación Android | Kotlin, Jetpack Compose, Material 3, Gradle y Android API 26 o superior. Usa API del sistema para voz, notificaciones y almacenamiento local. |
| Sitio público | React, TypeScript, Vite, Tailwind CSS y Three.js para la presentación visual y las animaciones. |
| Servicio web | Node.js y Express para cuentas, recuperación de acceso, sincronización y planes personalizados. |
| Datos remotos | PostgreSQL conserva cuentas, sesiones y el estado sincronizado de cada usuario. |
| Infraestructura | El sitio y el servicio se publican en Replit; la app se comunica con el servicio mediante HTTPS. |

## Persistencia y servicios en la nube

La aplicación conserva un estado local en el dispositivo. Cuando la persona tiene una sesión iniciada y conexión, sincroniza los datos de la cuenta con el servicio web y PostgreSQL. Si la sincronización no está disponible, el progreso local se conserva y la app muestra su estado de conexión. La sincronización se reintenta mientras la aplicación está activa; no se presenta como una tarea en segundo plano garantizada.

| Información | Dónde se conserva |
|---|---|
| Misiones, progreso, notas de texto, retos, monedas, rachas y sesiones completadas | Estado local y, con cuenta y conexión, sincronización con PostgreSQL. |
| Horarios y preferencias de recordatorios | Dispositivo Android; no se sincronizan entre dispositivos. |
| Temporizador en curso | Controles locales de la aplicación. |
| Dictado | Se conserva el texto reconocido o editado; no un historial de grabaciones. |

Servicios utilizados: Resend entrega correos para recuperar el acceso; Gemini genera propuestas de planes cuando se solicita esa función; el reconocedor de voz de Android transforma el dictado en texto. La disponibilidad del dictado depende del dispositivo y del servicio de voz. También es posible escribir manualmente.

La sincronización remota requiere una cuenta y conexión. El servicio valida la sesión, usa contraseñas protegidas mediante hash y registra operaciones de sincronización para evitar duplicados al reintentar. El token de sesión se guarda en almacenamiento privado de la app, que no se debe confundir con almacenamiento cifrado.

## Eventos, validaciones y respuestas

| Acción | Validación o respuesta |
|---|---|
| Crear cuenta | Verifica formato de correo y longitud mínima de contraseña; informa si el correo ya está registrado. |
| Crear misión | Requiere título, categoría y meta de 1 a 30 pasos; muestra el error antes de permitir guardar. |
| Avanzar misión | Suma un paso y evita entregar de nuevo una recompensa por la misma finalización. |
| Canjear recompensa | Solicita confirmación y comprueba saldo suficiente y que no exista un canje previo. |
| Dictar una nota | Solicita permiso; si no hay servicio, permiso o reconocimiento, informa el problema y permite escribir. |
| Generar plan | Envía el desafío cuando el usuario lo solicita; ante una falla remota puede ofrecer un borrador local. |
| Sincronizar | Requiere sesión; conserva los cambios locales ante fallos de red y evita duplicar operaciones reintentadas. |
| Borrar nota o reto | Pide confirmación antes de eliminar. |

## Pruebas y verificación

La cobertura automatizada existente incluye pruebas unitarias de progreso, rachas, recompensas y programación de recordatorios. También hay pruebas de interfaz para creación y avance de misiones, persistencia al reabrir la actividad, navegación adaptable a teléfono y tableta, etiquetas accesibles, texto ampliado, permisos de micrófono y eliminación confirmada de retos.

El backend cuenta con pruebas de reconciliación de datos y recuperación de contraseña, incluidas expiración, uso único y límites de solicitudes. La verificación de este documento no equivale a una prueba de todos los dispositivos o servicios externos: no se ejecutaron pruebas instrumentadas con un dispositivo conectado, ni se comprobó una entrega real de notificaciones tras reiniciar, los gestos físicos de TalkBack o el dictado sin conexión. Tampoco se presenta la integración con Gemini o Resend como prueba de funcionamiento de sus servicios externos.

## Retos técnicos y soluciones

- **Mantener el avance cuando la conexión falla:** se conserva el estado local y se vuelve a intentar la sincronización mientras la app está activa; las operaciones identificadas reducen duplicados.
- **Disponibilidad irregular del dictado:** el ingreso manual sigue disponible y los errores de permiso o servicio se comunican en pantalla.
- **Diferencias en las notificaciones de Android:** los avisos se programan en el dispositivo, pero el sistema operativo puede retrasarlos. La app contempla su reprogramación después de eventos del sistema, sin afirmar que la entrega sea inmediata.
- **Planes que dependen de un servicio remoto:** el plan se solicita explícitamente y puede mostrarse un borrador local si la generación remota no está disponible.
- **Prototipo frente a producto funcional:** el equipo amplió la propuesta visual inicial en Figma hasta una aplicación Android funcional, con persistencia, navegación y conexión con servicios.

## Retroalimentación de la primera fase

El equipo no recibió comentarios formales en la primera fase. La mejora documentada fue pasar de un prototipo de Figma a una aplicación funcional, ampliando la experiencia con las funciones de organización, enfoque y seguimiento descritas en este informe.

## Uso de inteligencia artificial y aportación del equipo

El equipo reporta que utilizó inteligencia artificial como apoyo en gran parte de la página web, especialmente su estructura y animaciones; en la creación de todas las imágenes de la mercancía; y en animaciones, iconos y conexiones con APIs de la aplicación. El equipo llevó la propuesta desde el prototipo de Figma hasta el producto funcional y empleó esa asistencia en la construcción de la experiencia. No se atribuyen tareas a integrantes individuales porque no se especificaron responsabilidades por persona.

## Enlaces del proyecto

- Repositorio: https://github.com/Fernando-Nun/TD-App
- Sitio público de TD-App: https://td-app.replit.app

El sitio público respondió correctamente al momento de elaborar este informe. El mismo dominio publica los servicios de la API que utiliza la aplicación.

## Conclusiones

TD-App convierte metas amplias en acciones pequeñas que se pueden iniciar, seguir y reconocer. La combinación de Pomodoro, misiones, retos, notas, recordatorios y recompensas integra en una sola aplicación varios apoyos para la organización personal. La persistencia local permite conservar el avance ante cortes de conexión y la cuenta habilita su sincronización remota. La experiencia se complementa con una página pública de descarga y una interfaz que considera accesibilidad y adaptación de pantalla. Las limitaciones de servicios de voz, sincronización y notificaciones se indican para que el alcance quede claro.

[[SCREENSHOTS]]
"""

PURPLE = "7C3AED"
DEEP_PURPLE = "4C1D95"
TEAL = "14B8A6"
GOLD = "FBBF24"
LAVENDER = "F7F3FF"
PALE_TEAL = "EFFAF8"
PALE_PURPLE = "F4F0FB"
INK = "21123B"
MUTED = "6D647A"
WHITE = "FFFFFF"
CONTENT_WIDTH = 10440

NS = {
    "w": "http://schemas.openxmlformats.org/wordprocessingml/2006/main",
    "r": "http://schemas.openxmlformats.org/officeDocument/2006/relationships",
    "wp": "http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing",
    "a": "http://schemas.openxmlformats.org/drawingml/2006/main",
    "pic": "http://schemas.openxmlformats.org/drawingml/2006/picture",
    "rel": "http://schemas.openxmlformats.org/package/2006/relationships",
    "ct": "http://schemas.openxmlformats.org/package/2006/content-types",
    "cp": "http://schemas.openxmlformats.org/package/2006/metadata/core-properties",
    "dc": "http://purl.org/dc/elements/1.1/",
    "dcterms": "http://purl.org/dc/terms/",
    "xsi": "http://www.w3.org/2001/XMLSchema-instance",
    "ep": "http://schemas.openxmlformats.org/officeDocument/2006/extended-properties",
    "vt": "http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes",
}

for prefix, uri in NS.items():
    if prefix not in {"ct", "cp", "dc", "dcterms", "xsi", "ep", "vt", "rel"}:
        ET.register_namespace(prefix, uri)

W = NS["w"]


def q(prefix: str, local: str) -> str:
    return f"{{{NS[prefix]}}}{local}"


def sub(parent: ET.Element, prefix: str, local: str, attrs: dict[str, str] | None = None) -> ET.Element:
    return ET.SubElement(parent, q(prefix, local), attrs or {})


def set_attr(element: ET.Element, local: str, value: str) -> None:
    element.set(q("w", local), value)


def add_text_run(
    paragraph: ET.Element,
    text: str,
    *,
    bold: bool = False,
    italic: bool = False,
    color: str | None = None,
    code: bool = False,
    size: int | None = None,
) -> None:
    if not text:
        return
    run = sub(paragraph, "w", "r")
    rpr = sub(run, "w", "rPr")
    if code:
        fonts = sub(rpr, "w", "rFonts")
        fonts.set(q("w", "ascii"), "Consolas")
        fonts.set(q("w", "hAnsi"), "Consolas")
        fonts.set(q("w", "eastAsia"), "Consolas")
        shade = sub(rpr, "w", "shd")
        set_attr(shade, "val", "clear")
        set_attr(shade, "fill", PALE_PURPLE)
    if bold:
        sub(rpr, "w", "b")
    if italic:
        sub(rpr, "w", "i")
    if color:
        c = sub(rpr, "w", "color")
        set_attr(c, "val", color)
    if size:
        s = sub(rpr, "w", "sz")
        set_attr(s, "val", str(size))
    t = sub(run, "w", "t")
    if text[:1].isspace() or text[-1:].isspace():
        t.set("{http://www.w3.org/XML/1998/namespace}space", "preserve")
    t.text = text


INLINE_PATTERN = re.compile(r"(`[^`]+`|\*\*.+?\*\*|\*[^*]+\*|\[[^\]]+\]\([^)]+\))")


def append_inline(paragraph: ET.Element, text: str, *, color: str | None = None, size: int | None = None) -> None:
    cursor = 0
    for match in INLINE_PATTERN.finditer(text):
        add_text_run(paragraph, text[cursor : match.start()], color=color, size=size)
        token = match.group(0)
        if token.startswith("`"):
            add_text_run(paragraph, token[1:-1], color=DEEP_PURPLE, code=True, size=size or 18)
        elif token.startswith("**"):
            add_text_run(paragraph, token[2:-2], bold=True, color=color, size=size)
        elif token.startswith("*"):
            add_text_run(paragraph, token[1:-1], italic=True, color=color, size=size)
        else:
            label = token[1 : token.index("](")]
            add_text_run(paragraph, label, color=PURPLE, size=size)
        cursor = match.end()
    add_text_run(paragraph, text[cursor:], color=color, size=size)


def paragraph_properties(
    paragraph: ET.Element,
    *,
    style: str | None = None,
    align: str | None = None,
    before: int | None = None,
    after: int | None = None,
    left: int | None = None,
    keep_next: bool = False,
    page_break_before: bool = False,
    shading: str | None = None,
    border_left: str | None = None,
) -> None:
    ppr = sub(paragraph, "w", "pPr")
    if style:
        pstyle = sub(ppr, "w", "pStyle")
        set_attr(pstyle, "val", style)
    if align:
        jc = sub(ppr, "w", "jc")
        set_attr(jc, "val", align)
    if before is not None or after is not None:
        spacing = sub(ppr, "w", "spacing")
        if before is not None:
            set_attr(spacing, "before", str(before))
        if after is not None:
            set_attr(spacing, "after", str(after))
    if left is not None:
        ind = sub(ppr, "w", "ind")
        set_attr(ind, "left", str(left))
    if keep_next:
        sub(ppr, "w", "keepNext")
    if page_break_before:
        sub(ppr, "w", "pageBreakBefore")
    if shading:
        shd = sub(ppr, "w", "shd")
        set_attr(shd, "val", "clear")
        set_attr(shd, "fill", shading)
    if border_left:
        borders = sub(ppr, "w", "pBdr")
        edge = sub(borders, "w", "left")
        set_attr(edge, "val", "single")
        set_attr(edge, "sz", "18")
        set_attr(edge, "space", "8")
        set_attr(edge, "color", border_left)


def add_paragraph(
    parent: ET.Element,
    text: str = "",
    *,
    style: str | None = None,
    align: str | None = None,
    before: int | None = None,
    after: int | None = None,
    left: int | None = None,
    keep_next: bool = False,
    page_break_before: bool = False,
    shading: str | None = None,
    border_left: str | None = None,
    color: str | None = None,
) -> ET.Element:
    paragraph = sub(parent, "w", "p")
    paragraph_properties(
        paragraph,
        style=style,
        align=align,
        before=before,
        after=after,
        left=left,
        keep_next=keep_next,
        page_break_before=page_break_before,
        shading=shading,
        border_left=border_left,
    )
    append_inline(paragraph, text, color=color)
    return paragraph


def add_page_break(parent: ET.Element) -> None:
    paragraph = sub(parent, "w", "p")
    run = sub(paragraph, "w", "r")
    br = sub(run, "w", "br")
    set_attr(br, "type", "page")


def add_logo(paragraph: ET.Element) -> None:
    run = sub(paragraph, "w", "r")
    drawing = sub(run, "w", "drawing")
    inline = sub(drawing, "wp", "inline", {
        "distT": "0", "distB": "0", "distL": "0", "distR": "0",
    })
    extent = sub(inline, "wp", "extent", {"cx": "1645920", "cy": "1645920"})
    sub(inline, "wp", "effectExtent", {"l": "0", "t": "0", "r": "0", "b": "0"})
    sub(inline, "wp", "docPr", {"id": "1", "name": "Logo TD-App", "descr": "Logotipo oficial de TD-App"})
    frame = sub(inline, "wp", "cNvGraphicFramePr")
    sub(frame, "a", "graphicFrameLocks", {"noChangeAspect": "1"})
    graphic = sub(inline, "a", "graphic")
    graphic_data = sub(graphic, "a", "graphicData", {
        "uri": "http://schemas.openxmlformats.org/drawingml/2006/picture",
    })
    pic = sub(graphic_data, "pic", "pic")
    nv = sub(pic, "pic", "nvPicPr")
    sub(nv, "pic", "cNvPr", {"id": "0", "name": "logo.png"})
    sub(nv, "pic", "cNvPicPr")
    blip_fill = sub(pic, "pic", "blipFill")
    sub(blip_fill, "a", "blip", {q("r", "embed"): "rId4"})
    stretch = sub(blip_fill, "a", "stretch")
    sub(stretch, "a", "fillRect")
    shape = sub(pic, "pic", "spPr")
    transform = sub(shape, "a", "xfrm")
    sub(transform, "a", "off", {"x": "0", "y": "0"})
    sub(transform, "a", "ext", {"cx": "1645920", "cy": "1645920"})
    geometry = sub(shape, "a", "prstGeom", {"prst": "rect"})
    sub(geometry, "a", "avLst")


def add_picture(
    paragraph: ET.Element,
    image_bytes: bytes,
    relationship_id: str,
    image_id: int,
    alt_text: str,
    *,
    max_width: int,
    max_height: int,
) -> None:
    if image_bytes[:8] != b"\x89PNG\r\n\x1a\n":
        raise ValueError(f"Expected a PNG screenshot for {alt_text}")
    pixel_width, pixel_height = struct.unpack(">II", image_bytes[16:24])
    if not pixel_width or not pixel_height:
        raise ValueError(f"Invalid screenshot dimensions for {alt_text}")

    aspect = pixel_width / pixel_height
    width = min(max_width, int(max_height * aspect))
    height = int(width / aspect)

    run = sub(paragraph, "w", "r")
    drawing = sub(run, "w", "drawing")
    inline = sub(drawing, "wp", "inline", {
        "distT": "0", "distB": "0", "distL": "0", "distR": "0",
    })
    sub(inline, "wp", "extent", {"cx": str(width), "cy": str(height)})
    sub(inline, "wp", "effectExtent", {"l": "0", "t": "0", "r": "0", "b": "0"})
    sub(inline, "wp", "docPr", {"id": str(image_id), "name": f"Evidencia {image_id}", "descr": alt_text})
    frame = sub(inline, "wp", "cNvGraphicFramePr")
    sub(frame, "a", "graphicFrameLocks", {"noChangeAspect": "1"})
    graphic = sub(inline, "a", "graphic")
    graphic_data = sub(graphic, "a", "graphicData", {
        "uri": "http://schemas.openxmlformats.org/drawingml/2006/picture",
    })
    pic = sub(graphic_data, "pic", "pic")
    nv = sub(pic, "pic", "nvPicPr")
    sub(nv, "pic", "cNvPr", {"id": str(image_id), "name": f"Evidencia {image_id}", "descr": alt_text})
    sub(nv, "pic", "cNvPicPr")
    blip_fill = sub(pic, "pic", "blipFill")
    sub(blip_fill, "a", "blip", {q("r", "embed"): relationship_id})
    stretch = sub(blip_fill, "a", "stretch")
    sub(stretch, "a", "fillRect")
    shape = sub(pic, "pic", "spPr")
    transform = sub(shape, "a", "xfrm")
    sub(transform, "a", "off", {"x": "0", "y": "0"})
    sub(transform, "a", "ext", {"cx": str(width), "cy": str(height)})
    geometry = sub(shape, "a", "prstGeom", {"prst": "rect"})
    sub(geometry, "a", "avLst")


def add_screenshot_gallery(parent: ET.Element) -> None:
    add_page_break(parent)
    add_paragraph(parent, "Evidencia visual", style="Heading1", before=250, after=110, keep_next=True)
    add_paragraph(
        parent,
        "Capturas de la aplicación y del sitio público. Los textos bajo cada imagen identifican la pantalla.",
        style="Normal",
        after=130,
    )
    for pair_start in range(0, len(SCREENSHOTS), 2):
        pair = SCREENSHOTS[pair_start : pair_start + 2]
        table = sub(parent, "w", "tbl")
        props = sub(table, "w", "tblPr")
        sub(props, "w", "tblW", {q("w", "w"): str(CONTENT_WIDTH), q("w", "type"): "dxa"})
        sub(props, "w", "tblLayout", {q("w", "type"): "fixed"})
        borders = sub(props, "w", "tblBorders")
        for edge_name in ("top", "left", "bottom", "right", "insideH", "insideV"):
            sub(borders, "w", edge_name, {q("w", "val"): "nil"})
        margins = sub(props, "w", "tblCellMar")
        for edge_name, value in (("top", "60"), ("start", "70"), ("bottom", "60"), ("end", "70")):
            sub(margins, "w", edge_name, {q("w", "w"): value, q("w", "type"): "dxa"})

        widths = [CONTENT_WIDTH] if len(pair) == 1 else [CONTENT_WIDTH // 2, CONTENT_WIDTH - CONTENT_WIDTH // 2]
        grid = sub(table, "w", "tblGrid")
        for cell_width in widths:
            sub(grid, "w", "gridCol", {q("w", "w"): str(cell_width)})
        row = sub(table, "w", "tr")
        sub(sub(row, "w", "trPr"), "w", "cantSplit")

        for index, (image_path, caption) in enumerate(pair):
            cell = sub(row, "w", "tc")
            tcpr = sub(cell, "w", "tcPr")
            cell_width = widths[index]
            sub(tcpr, "w", "tcW", {q("w", "w"): str(cell_width), q("w", "type"): "dxa"})
            if len(pair) == 1:
                sub(tcpr, "w", "gridSpan", {q("w", "val"): "2"})
            sub(tcpr, "w", "vAlign", {q("w", "val"): "center"})
            image_paragraph = sub(cell, "w", "p")
            paragraph_properties(image_paragraph, align="center", after=40)
            screen_index = pair_start + index
            image_width = int((5.8 if len(pair) == 1 else 3.0) * 914400)
            image_height = int((4.0 if len(pair) == 1 else 6.25) * 914400)
            add_picture(
                image_paragraph,
                image_path.read_bytes(),
                f"rId{5 + screen_index}",
                10 + screen_index,
                caption,
                max_width=image_width,
                max_height=image_height,
            )
            caption_paragraph = sub(cell, "w", "p")
            paragraph_properties(caption_paragraph, align="center", after=20)
            add_text_run(caption_paragraph, caption, color=MUTED, size=17)

        if pair_start + len(pair) < len(SCREENSHOTS):
            add_page_break(parent)


def make_table(parent: ET.Element, rows: list[list[str]], *, header: bool = True) -> ET.Element:
    if not rows:
        return sub(parent, "w", "tbl")
    columns = max(len(row) for row in rows)
    headers = [cell.strip().lower() for cell in rows[0]]
    if columns == 2 and headers and headers[0] in {"archivo", "parte", "tabla", "ruta", "capa", "componente"}:
        ratios = [0.31, 0.69]
    elif columns == 3:
        ratios = [0.28, 0.35, 0.37]
    else:
        ratios = [1 / columns] * columns
    widths = [int(CONTENT_WIDTH * ratio) for ratio in ratios]
    widths[-1] = CONTENT_WIDTH - sum(widths[:-1])

    table = sub(parent, "w", "tbl")
    props = sub(table, "w", "tblPr")
    sub(props, "w", "tblW", {q("w", "w"): str(CONTENT_WIDTH), q("w", "type"): "dxa"})
    sub(props, "w", "tblLayout", {q("w", "type"): "fixed"})
    borders = sub(props, "w", "tblBorders")
    for edge_name in ("top", "left", "bottom", "right", "insideH", "insideV"):
        edge = sub(borders, "w", edge_name)
        set_attr(edge, "val", "single")
        set_attr(edge, "sz", "5" if edge_name in {"top", "bottom"} else "3")
        set_attr(edge, "space", "0")
        set_attr(edge, "color", "DDD5EB")
    margins = sub(props, "w", "tblCellMar")
    for edge_name, value in (("top", "100"), ("start", "130"), ("bottom", "100"), ("end", "130")):
        sub(margins, "w", edge_name, {q("w", "w"): value, q("w", "type"): "dxa"})
    grid = sub(table, "w", "tblGrid")
    for width in widths:
        sub(grid, "w", "gridCol", {q("w", "w"): str(width)})

    for row_index, row_data in enumerate(rows):
        row = sub(table, "w", "tr")
        if row_index == 0 and header:
            sub(sub(row, "w", "trPr"), "w", "tblHeader")
        for col_index in range(columns):
            cell = sub(row, "w", "tc")
            tcpr = sub(cell, "w", "tcPr")
            sub(tcpr, "w", "tcW", {q("w", "w"): str(widths[col_index]), q("w", "type"): "dxa"})
            sub(tcpr, "w", "vAlign", {q("w", "val"): "top"})
            is_header = row_index == 0 and header
            if is_header:
                sub(tcpr, "w", "shd", {q("w", "fill"): DEEP_PURPLE, q("w", "val"): "clear"})
            elif row_index % 2 == 0:
                sub(tcpr, "w", "shd", {q("w", "fill"): "FAF8FD", q("w", "val"): "clear"})
            paragraph = sub(cell, "w", "p")
            paragraph_properties(paragraph, style="TableHeader" if is_header else "TableCell", after=0)
            text = row_data[col_index] if col_index < len(row_data) else ""
            append_inline(
                paragraph,
                text.strip(),
                color=WHITE if is_header else INK,
                size=18 if is_header else 18,
            )
    return table


def make_band(parent: ET.Element) -> None:
    table = sub(parent, "w", "tbl")
    props = sub(table, "w", "tblPr")
    sub(props, "w", "tblW", {q("w", "w"): str(CONTENT_WIDTH), q("w", "type"): "dxa"})
    sub(props, "w", "tblLayout", {q("w", "type"): "fixed"})
    grid = sub(table, "w", "tblGrid")
    for width in (CONTENT_WIDTH // 3, CONTENT_WIDTH // 3, CONTENT_WIDTH - 2 * (CONTENT_WIDTH // 3)):
        sub(grid, "w", "gridCol", {q("w", "w"): str(width)})
    row = sub(table, "w", "tr")
    sub(sub(row, "w", "trPr"), "w", "trHeight", {q("w", "val"): "140", q("w", "hRule"): "exact"})
    for color in (PURPLE, TEAL, GOLD):
        cell = sub(row, "w", "tc")
        tcpr = sub(cell, "w", "tcPr")
        sub(tcpr, "w", "tcW", {q("w", "w"): str(CONTENT_WIDTH // 3), q("w", "type"): "dxa"})
        sub(tcpr, "w", "shd", {q("w", "fill"): color, q("w", "val"): "clear"})
        paragraph = sub(cell, "w", "p")
        paragraph_properties(paragraph, after=0)


def make_cover(parent: ET.Element) -> None:
    add_paragraph(parent, "", after=240)
    make_band(parent)
    add_paragraph(parent, "", after=260)

    table = sub(parent, "w", "tbl")
    props = sub(table, "w", "tblPr")
    sub(props, "w", "tblW", {q("w", "w"): str(CONTENT_WIDTH), q("w", "type"): "dxa"})
    sub(props, "w", "tblLayout", {q("w", "type"): "fixed"})
    borders = sub(props, "w", "tblBorders")
    for edge_name in ("top", "left", "bottom", "right", "insideH", "insideV"):
        edge = sub(borders, "w", edge_name)
        set_attr(edge, "val", "single")
        set_attr(edge, "sz", "8" if edge_name in {"top", "bottom"} else "4")
        set_attr(edge, "color", "E6DCF5")
    margins = sub(props, "w", "tblCellMar")
    for edge_name, value in (("top", "520"), ("start", "420"), ("bottom", "520"), ("end", "420")):
        sub(margins, "w", edge_name, {q("w", "w"): value, q("w", "type"): "dxa"})
    sub(table, "w", "tblGrid")
    sub(table[-1], "w", "gridCol", {q("w", "w"): str(CONTENT_WIDTH)})
    row = sub(table, "w", "tr")
    sub(sub(row, "w", "trPr"), "w", "trHeight", {q("w", "val"): "7200", q("w", "hRule"): "atLeast"})
    cell = sub(row, "w", "tc")
    tcpr = sub(cell, "w", "tcPr")
    sub(tcpr, "w", "tcW", {q("w", "w"): str(CONTENT_WIDTH), q("w", "type"): "dxa"})
    sub(tcpr, "w", "shd", {q("w", "fill"): LAVENDER, q("w", "val"): "clear"})
    sub(tcpr, "w", "vAlign", {q("w", "val"): "center"})

    p = sub(cell, "w", "p")
    paragraph_properties(p, style="CoverLabel", align="center", after=230)
    add_text_run(p, "DOCUMENTO DE REFERENCIA", bold=True, color=TEAL, size=19)

    p = sub(cell, "w", "p")
    paragraph_properties(p, align="center", after=240)
    add_logo(p)

    p = sub(cell, "w", "p")
    paragraph_properties(p, style="CoverTitle", align="center", after=120)
    add_text_run(p, "TD-App", bold=True, color=DEEP_PURPLE, size=56)

    p = sub(cell, "w", "p")
    paragraph_properties(p, style="CoverSubtitle", align="center", after=260)
    add_text_run(p, "Proyecto, funcionamiento y evidencias", color=INK, size=30)

    p = sub(cell, "w", "p")
    paragraph_properties(p, style="CoverMeta", align="center", after=80)
    add_text_run(p, "Equipo Pingüino  ·  Septiembre de 2026", color=MUTED, size=21)

    p = sub(cell, "w", "p")
    paragraph_properties(p, style="CoverTagline", align="center", before=220)
    add_text_run(p, "Pequeños pasos, grandes avances", italic=True, color=PURPLE, size=22)

    add_paragraph(parent, "", after=220)
    add_paragraph(parent, "Funciones principales · persistencia · alcance actual", align="center", color=MUTED, after=180)
    add_page_break(parent)


def add_architecture_table(parent: ET.Element) -> None:
    rows = [
        ["Capa", "Recorrido y responsabilidad"],
        ["Android", "MainActivity → Jetpack Compose → TDCoinsApp / TDCoinsContent → pantallas y componentes."],
        ["Guardado local", "AppPersistence guarda el snapshot en SharedPreferences; AppNotifications mantiene recordatorios locales."],
        ["Conexión", "SyncClient envía solicitudes HTTPS JSON a la API configurada en SYNC_API_URL."],
        ["API y datos", "td-app.replit.app → API Node/Express → PostgreSQL: autenticación, sincronización y estado por cuenta."],
        ["Servicios", "Resend envía códigos de recuperación; Gemini ayuda a generar planes de retos."],
    ]
    make_table(parent, rows)


def parse_table(lines: list[str]) -> list[list[str]]:
    rows: list[list[str]] = []
    for line in lines:
        stripped = line.strip().strip("|")
        cells = [cell.strip() for cell in stripped.split("|")]
        if cells and all(re.fullmatch(r":?-{3,}:?", cell.replace(" ", "")) for cell in cells):
            continue
        rows.append(cells)
    return rows


def is_table_line(line: str) -> bool:
    return line.strip().startswith("|") and "|" in line.strip()[1:]


def build_document(markdown: str) -> bytes:
    document = ET.Element(q("w", "document"))
    body = sub(document, "w", "body")

    make_cover(body)

    lines = markdown.splitlines()
    i = 0
    while i < len(lines):
        line = lines[i].rstrip()
        if not line.strip():
            i += 1
            continue
        if line.strip() == "[[SCREENSHOTS]]":
            add_screenshot_gallery(body)
            i += 1
            continue
        if line.startswith("# "):
            i += 1
            continue
        if line.startswith("```"):
            language = line[3:].strip().lower()
            i += 1
            block: list[str] = []
            while i < len(lines) and not lines[i].startswith("```"):
                block.append(lines[i])
                i += 1
            i += 1
            if language == "mermaid":
                add_architecture_table(body)
            elif block:
                for code_line in block:
                    paragraph = sub(body, "w", "p")
                    paragraph_properties(paragraph, style="CodeBlock", after=0, shading="F7F5FA")
                    add_text_run(paragraph, code_line or " ", color=DEEP_PURPLE, code=True, size=17)
            continue
        if line.startswith("## "):
            add_paragraph(body, line[3:].strip(), style="Heading1", before=250, after=110, keep_next=True)
            i += 1
            continue
        if line.startswith("### "):
            add_paragraph(body, line[4:].strip(), style="Heading2", before=190, after=80, keep_next=True)
            i += 1
            continue
        if line.startswith("#### "):
            add_paragraph(body, line[5:].strip(), style="Heading3", before=140, after=60, keep_next=True)
            i += 1
            continue
        if is_table_line(line):
            table_lines: list[str] = []
            while i < len(lines) and is_table_line(lines[i]):
                table_lines.append(lines[i])
                i += 1
            make_table(body, parse_table(table_lines))
            add_paragraph(body, "", after=100)
            continue
        if line.startswith(">"):
            quote_lines = []
            while i < len(lines) and lines[i].lstrip().startswith(">"):
                quote_lines.append(lines[i].lstrip()[1:].strip())
                i += 1
            add_paragraph(
                body,
                " ".join(quote_lines),
                style="Quote",
                before=100,
                after=140,
                left=250,
                shading=PALE_TEAL,
                border_left=TEAL,
            )
            continue
        if re.match(r"^\s*[-*]\s+", line):
            add_paragraph(body, "•  " + re.sub(r"^\s*[-*]\s+", "", line), style="ListItem", after=45)
            i += 1
            continue
        ordered = re.match(r"^\s*(\d+)\.\s+(.*)$", line)
        if ordered:
            add_paragraph(body, f"{ordered.group(1)}.  {ordered.group(2)}", style="ListItem", after=55)
            i += 1
            continue
        if line.strip() in {"---", "***", "___"}:
            p = sub(body, "w", "p")
            ppr = sub(p, "w", "pPr")
            borders = sub(ppr, "w", "pBdr")
            bottom = sub(borders, "w", "bottom")
            set_attr(bottom, "val", "single")
            set_attr(bottom, "sz", "8")
            set_attr(bottom, "space", "1")
            set_attr(bottom, "color", TEAL)
            i += 1
            continue

        paragraph_lines = [line.strip()]
        i += 1
        while i < len(lines):
            candidate = lines[i].rstrip()
            if (
                not candidate.strip()
                or candidate.startswith(("#", "```", ">", "|"))
                or re.match(r"^\s*[-*]\s+", candidate)
                or re.match(r"^\s*\d+\.\s+", candidate)
                or candidate.strip() in {"---", "***", "___"}
            ):
                break
            paragraph_lines.append(candidate.strip())
            i += 1
        add_paragraph(body, " ".join(paragraph_lines), style="Normal", after=100)

    sect = sub(body, "w", "sectPr")
    sub(sect, "w", "headerReference", {q("w", "type"): "default", q("r", "id"): "rId2"})
    sub(sect, "w", "footerReference", {q("w", "type"): "default", q("r", "id"): "rId3"})
    sub(sect, "w", "pgSz", {q("w", "w"): "12240", q("w", "h"): "15840"})
    sub(sect, "w", "pgMar", {
        q("w", "top"): "1080",
        q("w", "right"): "900",
        q("w", "bottom"): "1080",
        q("w", "left"): "900",
        q("w", "header"): "480",
        q("w", "footer"): "480",
        q("w", "gutter"): "0",
    })
    return ET.tostring(document, encoding="utf-8", xml_declaration=True)


def make_styles() -> bytes:
    styles = ET.Element(q("w", "styles"))
    defaults = sub(styles, "w", "docDefaults")
    rdefault = sub(defaults, "w", "rPrDefault")
    rpr = sub(rdefault, "w", "rPr")
    fonts = sub(rpr, "w", "rFonts")
    for attr in ("ascii", "hAnsi", "eastAsia", "cs"):
        set_attr(fonts, attr, "Arial")
    size = sub(rpr, "w", "sz")
    set_attr(size, "val", "22")
    color = sub(rpr, "w", "color")
    set_attr(color, "val", INK)
    pdefault = sub(defaults, "w", "pPrDefault")
    pp = sub(pdefault, "w", "pPr")
    spacing = sub(pp, "w", "spacing")
    set_attr(spacing, "after", "100")
    set_attr(spacing, "line", "285")
    set_attr(spacing, "lineRule", "auto")

    style_specs = [
        ("Normal", "paragraph", None, None, None, None),
        ("Heading1", "paragraph", "36", PURPLE, "220", "120"),
        ("Heading2", "paragraph", "28", DEEP_PURPLE, "170", "90"),
        ("Heading3", "paragraph", "24", TEAL, "130", "70"),
        ("CoverTitle", "paragraph", "56", DEEP_PURPLE, "0", "80"),
        ("CoverSubtitle", "paragraph", "32", INK, "0", "100"),
        ("CoverMeta", "paragraph", "22", MUTED, "0", "40"),
        ("CoverTagline", "paragraph", "24", PURPLE, "0", "40"),
        ("CoverLabel", "paragraph", "20", TEAL, "0", "40"),
        ("TableCell", "paragraph", "18", INK, "0", "0"),
        ("TableHeader", "paragraph", "18", WHITE, "0", "0"),
        ("ListItem", "paragraph", "22", INK, "0", "20"),
        ("Quote", "paragraph", "22", INK, "0", "0"),
        ("CodeBlock", "paragraph", "18", DEEP_PURPLE, "0", "0"),
        ("TOCLine", "paragraph", "22", DEEP_PURPLE, "0", "40"),
        ("TOCNote", "paragraph", "20", MUTED, "0", "0"),
    ]
    for style_id, style_type, size_val, style_color, before, after in style_specs:
        style = sub(styles, "w", "style", {
            q("w", "type"): style_type,
            q("w", "styleId"): style_id,
        })
        if style_id == "Normal":
            set_attr(style, "default", "1")
        sub(style, "w", "name", {q("w", "val"): style_id})
        if style_id.startswith("Heading"):
            outline = int(style_id[-1]) - 1
            sub(style, "w", "basedOn", {q("w", "val"): "Normal"})
            ppr = sub(style, "w", "pPr")
            sub(ppr, "w", "keepNext")
            sub(ppr, "w", "keepLines")
            sub(ppr, "w", "outlineLvl", {q("w", "val"): str(outline)})
            space = sub(ppr, "w", "spacing")
            set_attr(space, "before", before)
            set_attr(space, "after", after)
        rpr = sub(style, "w", "rPr")
        if size_val:
            sz = sub(rpr, "w", "sz")
            set_attr(sz, "val", size_val)
        if style_color:
            c = sub(rpr, "w", "color")
            set_attr(c, "val", style_color)
        if style_id.startswith("Heading") or style_id.startswith("Cover"):
            sub(rpr, "w", "b")
        if style_id == "CoverTagline":
            sub(rpr, "w", "i")
    return ET.tostring(styles, encoding="utf-8", xml_declaration=True)


def make_header() -> bytes:
    root = ET.Element(q("w", "hdr"))
    p = sub(root, "w", "p")
    ppr = sub(p, "w", "pPr")
    spacing = sub(ppr, "w", "spacing")
    set_attr(spacing, "after", "90")
    borders = sub(ppr, "w", "pBdr")
    edge = sub(borders, "w", "bottom")
    set_attr(edge, "val", "single")
    set_attr(edge, "sz", "7")
    set_attr(edge, "space", "5")
    set_attr(edge, "color", TEAL)
    add_text_run(p, "TD-APP", bold=True, color=DEEP_PURPLE, size=17)
    add_text_run(p, "     DOCUMENTACIÓN DEL PROYECTO Y EVIDENCIAS", color=MUTED, size=17)
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def make_footer() -> bytes:
    root = ET.Element(q("w", "ftr"))
    p = sub(root, "w", "p")
    ppr = sub(p, "w", "pPr")
    sub(ppr, "w", "jc", {q("w", "val"): "center"})
    add_text_run(p, "TD-App  ·  Documento de referencia  ·  ", color=MUTED, size=16)
    run = sub(p, "w", "r")
    field = sub(run, "w", "fldSimple", {q("w", "instr"): "PAGE"})
    field_run = sub(field, "w", "r")
    t = sub(field_run, "w", "t")
    t.text = "1"
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def make_relationships() -> bytes:
    root = ET.Element(q("rel", "Relationships"))
    entries = [
        ("rId1", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles", "styles.xml"),
        ("rId2", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/header", "header1.xml"),
        ("rId3", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/footer", "footer1.xml"),
        ("rId4", "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image", "media/logo.png"),
    ]
    entries.extend(
        (
            f"rId{5 + index}",
            "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image",
            f"media/evidence-{index + 1:02}.png",
        )
        for index in range(len(SCREENSHOTS))
    )
    for rid, rel_type, target in entries:
        sub(root, "rel", "Relationship", {"Id": rid, "Type": rel_type, "Target": target})
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def make_root_relationships() -> bytes:
    root = ET.Element(q("rel", "Relationships"))
    sub(root, "rel", "Relationship", {
        "Id": "rId1",
        "Type": "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument",
        "Target": "word/document.xml",
    })
    sub(root, "rel", "Relationship", {
        "Id": "rId2",
        "Type": "http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties",
        "Target": "docProps/core.xml",
    })
    sub(root, "rel", "Relationship", {
        "Id": "rId3",
        "Type": "http://schemas.openxmlformats.org/officeDocument/2006/relationships/extended-properties",
        "Target": "docProps/app.xml",
    })
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def make_content_types() -> bytes:
    root = ET.Element(q("ct", "Types"))
    sub(root, "ct", "Default", {"Extension": "rels", "ContentType": "application/vnd.openxmlformats-package.relationships+xml"})
    sub(root, "ct", "Default", {"Extension": "xml", "ContentType": "application/xml"})
    sub(root, "ct", "Default", {"Extension": "png", "ContentType": "image/png"})
    overrides = [
        ("/word/document.xml", "application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"),
        ("/word/styles.xml", "application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"),
        ("/word/header1.xml", "application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml"),
        ("/word/footer1.xml", "application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml"),
        ("/docProps/core.xml", "application/vnd.openxmlformats-package.core-properties+xml"),
        ("/docProps/app.xml", "application/vnd.openxmlformats-officedocument.extended-properties+xml"),
    ]
    for part, content_type in overrides:
        sub(root, "ct", "Override", {"PartName": part, "ContentType": content_type})
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def make_core_properties() -> bytes:
    root = ET.Element(q("cp", "coreProperties"))
    title = sub(root, "dc", "title")
    title.text = "TD-App — Documentación del proyecto"
    creator = sub(root, "dc", "creator")
    creator.text = "TD-App"
    subject = sub(root, "dc", "subject")
    subject.text = "Proyecto, funciones, tecnologías, verificación y evidencias"
    language = sub(root, "dc", "language")
    language.text = "es-MX"
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def make_app_properties() -> bytes:
    root = ET.Element(q("ep", "Properties"))
    sub(root, "ep", "Application").text = "TD-App"
    sub(root, "ep", "DocSecurity").text = "0"
    sub(root, "ep", "ScaleCrop").text = "false"
    heading_pairs = sub(root, "ep", "HeadingPairs")
    vector = sub(heading_pairs, "vt", "vector", {"size": "2", "baseType": "variant"})
    variant = sub(vector, "vt", "variant")
    sub(variant, "vt", "lpstr").text = "Title"
    variant = sub(vector, "vt", "variant")
    sub(variant, "vt", "i4").text = "1"
    titles = sub(root, "ep", "TitlesOfParts")
    title_vector = sub(titles, "vt", "vector", {"size": "1", "baseType": "lpstr"})
    sub(title_vector, "vt", "lpstr").text = "TD-App — Documentación del proyecto"
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def write_docx(markdown: str) -> None:
    OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    document_xml = build_document(markdown)
    with zipfile.ZipFile(OUTPUT, "w", compression=zipfile.ZIP_DEFLATED) as docx:
        docx.writestr("[Content_Types].xml", make_content_types())
        docx.writestr("_rels/.rels", make_root_relationships())
        docx.writestr("word/document.xml", document_xml)
        docx.writestr("word/_rels/document.xml.rels", make_relationships())
        docx.writestr("word/styles.xml", make_styles())
        docx.writestr("word/header1.xml", make_header())
        docx.writestr("word/footer1.xml", make_footer())
        docx.writestr("word/media/logo.png", LOGO.read_bytes())
        for index, (image_path, _) in enumerate(SCREENSHOTS, start=1):
            docx.writestr(f"word/media/evidence-{index:02}.png", image_path.read_bytes())
        docx.writestr("docProps/core.xml", make_core_properties())
        docx.writestr("docProps/app.xml", make_app_properties())


def inline_html(text: str) -> str:
    escaped = html_lib.escape(text, quote=False)
    escaped = re.sub(r"`([^`]+)`", r"<code>\1</code>", escaped)
    escaped = re.sub(r"\*\*(.+?)\*\*", r"<strong>\1</strong>", escaped)
    escaped = re.sub(r"\*([^*]+)\*", r"<em>\1</em>", escaped)
    return escaped


def screenshot_gallery_html() -> str:
    pieces = [
        '<section class="evidence-page">',
        "<h1>Evidencia visual</h1>",
        "<p>Capturas de la aplicación y del sitio público. Los textos bajo cada imagen identifican la pantalla.</p>",
    ]
    for pair_start in range(0, len(SCREENSHOTS), 2):
        pair = SCREENSHOTS[pair_start : pair_start + 2]
        pieces.append('<div class="evidence-pair">')
        for image_path, caption in pair:
            encoded = base64.b64encode(image_path.read_bytes()).decode("ascii")
            alt = html_lib.escape(caption, quote=True)
            pieces.extend([
                '<figure class="evidence-card">',
                f'<img src="data:image/png;base64,{encoded}" alt="{alt}">',
                f"<figcaption>{inline_html(caption)}</figcaption>",
                "</figure>",
            ])
        pieces.append("</div>")
    pieces.append("</section>")
    return "".join(pieces)


def markdown_to_html(markdown: str) -> str:
    lines = markdown.splitlines()
    output: list[str] = []
    i = 0
    while i < len(lines):
        line = lines[i].rstrip()
        if not line.strip():
            i += 1
            continue
        if line.strip() == "[[SCREENSHOTS]]":
            output.append(screenshot_gallery_html())
            i += 1
            continue
        if line.startswith("# "):
            i += 1
            continue
        if line.startswith("## "):
            output.append(f"<h2>{inline_html(line[3:].strip())}</h2>")
            i += 1
            continue
        if line.startswith("### "):
            output.append(f"<h3>{inline_html(line[4:].strip())}</h3>")
            i += 1
            continue
        if is_table_line(line):
            table_lines: list[str] = []
            while i < len(lines) and is_table_line(lines[i]):
                table_lines.append(lines[i])
                i += 1
            rows = parse_table(table_lines)
            if rows:
                output.append("<table><thead><tr>")
                output.extend(f"<th>{inline_html(cell)}</th>" for cell in rows[0])
                output.append("</tr></thead><tbody>")
                for row in rows[1:]:
                    output.append("<tr>")
                    output.extend(f"<td>{inline_html(cell)}</td>" for cell in row)
                    output.append("</tr>")
                output.append("</tbody></table>")
            continue
        if re.match(r"^\s*[-*]\s+", line):
            output.append("<ul>")
            while i < len(lines) and re.match(r"^\s*[-*]\s+", lines[i]):
                item = re.sub(r"^\s*[-*]\s+", "", lines[i].strip())
                output.append(f"<li>{inline_html(item)}</li>")
                i += 1
            output.append("</ul>")
            continue
        ordered = re.match(r"^\s*\d+\.\s+", line)
        if ordered:
            output.append("<ol>")
            while i < len(lines) and re.match(r"^\s*\d+\.\s+", lines[i]):
                item = re.sub(r"^\s*\d+\.\s+", "", lines[i].strip())
                output.append(f"<li>{inline_html(item)}</li>")
                i += 1
            output.append("</ol>")
            continue
        if line.lstrip().startswith(">"):
            quote_lines: list[str] = []
            while i < len(lines) and lines[i].lstrip().startswith(">"):
                quote_lines.append(lines[i].lstrip()[1:].strip())
                i += 1
            output.append(f"<blockquote>{inline_html(' '.join(quote_lines))}</blockquote>")
            continue

        paragraph_lines = [line.strip()]
        i += 1
        while i < len(lines):
            candidate = lines[i].rstrip()
            if (
                not candidate.strip()
                or candidate.startswith(("#", "|", ">", "[[SCREENSHOTS]]"))
                or re.match(r"^\s*[-*]\s+", candidate)
                or re.match(r"^\s*\d+\.\s+", candidate)
            ):
                break
            paragraph_lines.append(candidate.strip())
            i += 1
        output.append(f"<p>{inline_html(' '.join(paragraph_lines))}</p>")
    return "\n".join(output)


def make_print_html(markdown: str) -> str:
    logo = base64.b64encode(LOGO.read_bytes()).decode("ascii")
    body = markdown_to_html(markdown)
    return f"""<!doctype html>
<html lang="es">
<head>
  <meta charset="utf-8">
  <title>TD-App — Documentación del proyecto</title>
  <style>
    @page {{ size: letter; margin: 0.65in 0.68in 0.7in; }}
    * {{ box-sizing: border-box; }}
    body {{ margin: 0; color: #21123B; font: 10pt/1.45 Arial, sans-serif; }}
    .cover {{
      min-height: 9.2in; margin: 0; padding: 0.55in; display: flex;
      flex-direction: column; align-items: center; justify-content: center;
      text-align: center; background: #F7F3FF; break-after: page;
    }}
    .cover-band {{ width: 100%; height: 0.12in; margin-bottom: 0.65in;
      background: linear-gradient(90deg, #7C3AED 0 34%, #14B8A6 34% 67%, #FBBF24 67%); }}
    .cover img {{ width: 1.15in; height: 1.15in; object-fit: contain; margin: 0.25in 0; }}
    .cover-label {{ color: #14B8A6; letter-spacing: 0.12em; font-weight: bold; font-size: 9pt; }}
    .cover h1 {{ margin: 0.05in 0; font-size: 42pt; color: #4C1D95; }}
    .cover h2 {{ margin: 0.1in 0 0.25in; border: 0; font-size: 19pt; color: #21123B; }}
    .cover-meta {{ color: #6D647A; font-size: 11pt; }}
    .cover-tagline {{ margin-top: 0.55in; font-size: 13pt; color: #7C3AED; font-style: italic; }}
    h1, h2, h3 {{ color: #4C1D95; break-after: avoid; }}
    h1 {{ margin: 0 0 0.16in; padding-bottom: 0.08in; font-size: 20pt;
      border-bottom: 2px solid #14B8A6; }}
    h2 {{ margin: 0.22in 0 0.09in; font-size: 15pt; }}
    h3 {{ margin: 0.16in 0 0.06in; font-size: 12pt; color: #14B8A6; }}
    p {{ margin: 0 0 0.11in; }}
    ul, ol {{ margin: 0.03in 0 0.15in; padding-left: 0.25in; }}
    li {{ margin: 0 0 0.055in; }}
    table {{ width: 100%; margin: 0.1in 0 0.18in; border-collapse: collapse; font-size: 9pt; }}
    th, td {{ padding: 0.07in 0.09in; border: 1px solid #DDD5EB; text-align: left; vertical-align: top; }}
    th {{ color: #fff; background: #4C1D95; }}
    tbody tr:nth-child(even) {{ background: #FAF8FD; }}
    strong {{ color: #4C1D95; }}
    code {{ padding: 0.01in 0.03in; color: #4C1D95; background: #F4F0FB; }}
    .evidence-page {{ break-before: page; }}
    .evidence-pair {{ display: grid; grid-template-columns: 1fr 1fr; gap: 0.18in;
      align-items: start; break-inside: avoid; }}
    .evidence-pair + .evidence-pair {{ break-before: page; }}
    .evidence-card {{ margin: 0; padding: 0.08in; text-align: center; break-inside: avoid; }}
    .evidence-card img {{ display: block; max-width: 100%; max-height: 6.25in;
      width: auto; height: auto; margin: 0 auto 0.1in; object-fit: contain; }}
    .evidence-card figcaption {{ color: #6D647A; font-size: 9pt; }}
    .evidence-card:only-child {{ grid-column: 1 / -1; }}
    .evidence-card:only-child img {{ max-height: 4in; max-width: 5.8in; }}
    @media print {{
      .evidence-pair, table, tr {{ break-inside: avoid; }}
      .cover {{ height: 9.2in; }}
    }}
  </style>
</head>
<body>
  <section class="cover">
    <div class="cover-band"></div>
    <p class="cover-label">DOCUMENTO DEL PROYECTO</p>
    <img src="data:image/png;base64,{logo}" alt="Logotipo de TD-App">
    <h1>TD-App</h1>
    <h2>Proyecto, funcionamiento y evidencias</h2>
    <p class="cover-meta">Equipo Pingüino · Septiembre de 2026</p>
    <p class="cover-tagline">Pequeños pasos, grandes avances</p>
  </section>
  <main>{body}</main>
</body>
</html>"""


def write_pdf(markdown: str) -> None:
    chromium = shutil.which("chromium") or "/repl/tools/bin/chromium"
    if not Path(chromium).is_file():
        raise RuntimeError("Chromium is required to export the PDF.")
    PDF_OUTPUT.parent.mkdir(parents=True, exist_ok=True)
    with tempfile.TemporaryDirectory(prefix="tdapp-document-") as temp_dir:
        temp_path = Path(temp_dir)
        html_path = temp_path / "document.html"
        html_path.write_text(make_print_html(markdown), encoding="utf-8")
        command = [
            chromium,
            "--headless=new",
            "--disable-gpu",
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--no-pdf-header-footer",
            f"--user-data-dir={temp_path / 'profile'}",
            f"--print-to-pdf={PDF_OUTPUT}",
            html_path.as_uri(),
        ]
        result = subprocess.run(command, capture_output=True, text=True, timeout=120)
        if result.returncode:
            raise RuntimeError(f"Chromium PDF export failed: {result.stderr[-3000:]}")
    if not PDF_OUTPUT.is_file() or PDF_OUTPUT.stat().st_size < 1000:
        raise RuntimeError("Chromium did not create a usable PDF.")


def main() -> None:
    missing = [str(path) for path in [LOGO, *(image for image, _ in SCREENSHOTS)] if not path.is_file()]
    if missing:
        raise SystemExit("Missing document assets: " + ", ".join(missing))
    write_docx(GUIDE_MARKDOWN)
    write_pdf(GUIDE_MARKDOWN)
    print(f"Created {OUTPUT.relative_to(ROOT)} ({OUTPUT.stat().st_size:,} bytes)")
    print(f"Created {PDF_OUTPUT.relative_to(ROOT)} ({PDF_OUTPUT.stat().st_size:,} bytes)")


if __name__ == "__main__":
    main()