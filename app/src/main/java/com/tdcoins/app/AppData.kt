package com.tdcoins.app

import java.util.UUID

fun initialMissions() = emptyList<Mission>()

fun createPersonalChallenge(text: String): VoiceChallenge {
    val clean = text.trim()
    val goal = clean.take(140).ifBlank { "tu reto" }
    return VoiceChallenge(
        id = UUID.randomUUID().toString(),
        text = clean,
        icon = "",
        reminders = listOf(
            "Lee este objetivo al comenzar el día: \"$goal\"",
            "Reserva 10 minutos para avanzar en \"$goal\"",
            "Antes de terminar el día, anota qué hiciste sobre \"$goal\"",
        ),
        plan = listOf(
            "Escribe qué resultado concreto quieres lograr con \"$goal\"",
            "Elige una primera acción de 10 minutos relacionada con \"$goal\"",
            "Haz esa acción en un horario específico y registra el avance",
            "Revisa lo que ocurrió y decide el siguiente paso para \"$goal\"",
        ),
    )
}

fun storeItems() = listOf(
    StoreItem(
        "1",
        "Bola Anti-Estrés TDAH",
        120,
        R.drawable.pelota,
        "Bola de silicona con el logo TD-Coins para manejar la ansiedad",
        StoreTag.STRESS,
    ),
    StoreItem(
        "2",
        "Llavero Fuerza Mental",
        80,
        R.drawable.llavero,
        "Llavero metálico con el mantra «Enfoque es mi superpoder»",
        StoreTag.ACCESSORY,
    ),
    StoreItem(
        "3",
        "Taza Cerebro en Llamas",
        200,
        R.drawable.taza,
        "Taza de 350 ml con frases motivacionales para TDAH",
        StoreTag.LIFESTYLE,
    ),
    StoreItem(
        "4",
        "Gorra TD-Coins",
        350,
        R.drawable.gorra,
        "Gorra snapback bordada con el logo oficial de TD-Coins",
        StoreTag.CLOTHING,
    ),
    StoreItem(
        "5",
        "Playera Superhéroe TDAH",
        450,
        R.drawable.playera,
        "Playera unisex «Mi TDAH es mi superpoder»",
        StoreTag.CLOTHING,
    ),
    StoreItem(
        "6",
        "Funda Protectora TD-App",
        180,
        R.drawable.funda,
        "Funda para celular con diseño anti-distracción",
        StoreTag.TECH,
    ),
    StoreItem(
        "7",
        "Mochila Explorador",
        800,
        R.drawable.mochila,
        "Mochila con múltiples compartimentos y diseño TD-Coins",
        StoreTag.LIFESTYLE,
    ),
    StoreItem(
        "8",
        "Pack Inicio Hero",
        280,
        R.drawable.regalo,
        "Set llavero + bola anti-estrés + calcomanías exclusivas",
        StoreTag.PACK,
    ),
)

fun voiceChallenges() = emptyList<VoiceChallenge>()