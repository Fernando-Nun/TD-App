#!/usr/bin/env python3
"""Build the branded TD-App Word guide using only Python's standard library."""

from __future__ import annotations

import re
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET


ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "docs" / "ARQUITECTURA_Y_PRESENTACION_TD_APP.md"
LOGO = ROOT / "app" / "src" / "main" / "res" / "drawable" / "logo.png"
OUTPUT = ROOT / "docs" / "GUIA_TD_APP.docx"

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
    add_text_run(p, "Guía técnica y de presentación", color=INK, size=32)

    p = sub(cell, "w", "p")
    paragraph_properties(p, style="CoverMeta", align="center", after=80)
    add_text_run(p, "Android  ·  Jetpack Compose  ·  Sincronización  ·  Resend", color=MUTED, size=21)

    p = sub(cell, "w", "p")
    paragraph_properties(p, style="CoverTagline", align="center", before=220)
    add_text_run(p, "Pequeños pasos, grandes avances", italic=True, color=PURPLE, size=22)

    add_paragraph(parent, "", after=220)
    add_paragraph(parent, "Arquitectura actual · funciones implementadas · presentación al cliente", align="center", color=MUTED, after=180)
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

    headings = [line[3:].strip() for line in markdown.splitlines() if line.startswith("## ")]
    add_paragraph(body, "Contenido", style="Heading1", after=160)
    for title in headings:
        add_paragraph(body, "•  " + title, style="TOCLine", after=75)
    add_paragraph(body, "Guía preparada a partir del código actual del proyecto.", style="TOCNote", before=180, color=MUTED)
    add_page_break(body)

    lines = markdown.splitlines()
    i = 0
    while i < len(lines):
        line = lines[i].rstrip()
        if not line.strip():
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
    add_text_run(p, "     GUÍA TÉCNICA Y DE PRESENTACIÓN", color=MUTED, size=17)
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
    title.text = "TD-App — Guía técnica y de presentación"
    creator = sub(root, "dc", "creator")
    creator.text = "TD-App"
    subject = sub(root, "dc", "subject")
    subject.text = "Jetpack Compose, arquitectura Android, sincronización y presentación comercial"
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
    sub(title_vector, "vt", "lpstr").text = "TD-App — Guía técnica y de presentación"
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)


def write_docx(markdown: str) -> None:
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
        docx.writestr("docProps/core.xml", make_core_properties())
        docx.writestr("docProps/app.xml", make_app_properties())


def main() -> None:
    if not SOURCE.is_file():
        raise SystemExit(f"Markdown source not found: {SOURCE}")
    if not LOGO.is_file():
        raise SystemExit(f"App logo not found: {LOGO}")
    write_docx(SOURCE.read_text(encoding="utf-8"))
    print(f"Created {OUTPUT.relative_to(ROOT)} ({OUTPUT.stat().st_size:,} bytes)")


if __name__ == "__main__":
    main()