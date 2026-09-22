package com.tdcoins.app

import java.util.UUID

fun initialMissions() = emptyList<Mission>()

fun createPersonalChallenge(text: String): VoiceChallenge {
    val clean = text.trim()
    return VoiceChallenge(
        id = UUID.randomUUID().toString(),
        text = clean,
        icon = "🌱",
        reminders = listOf(
            "Revisa tu objetivo al iniciar el día",
            "Haz una acción pequeña antes de posponerlo",
            "Celebra cada avance observable",
        ),
        plan = listOf(
            "Describe el resultado que quieres conseguir",
            "Divide el objetivo en el paso más pequeño",
            "Reserva un momento concreto para hacerlo",
            "Revisa el avance y ajusta el siguiente paso",
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

fun voiceChallenges() = emptyList<VoiceChallenge>() /* Built-in challenges were removed; user goals are generated in the profile. */
/*
    VoiceChallenge(
        "sleep",
        "Se me dificulta dormir",
        "🌙",
        listOf(
            "Apaga pantallas 30 min antes de dormir",
            "Recordatorio a las 9:30 PM: Rutina nocturna",
            "Alarma suave a las 10:00 PM: Hora de descansar",
        ),
        listOf(
            "Crea una rutina nocturna de 15 min",
            "Usa luz tenue por la noche",
            "Escribe 3 cosas buenas del día antes de dormir",
            "Prueba respiración 4-7-8 para relajarte",
        ),
    ),
    VoiceChallenge(
        "focus",
        "Se me dificulta poner atención",
        "🎯",
        listOf(
            "Pomodoro activo: 25 min de enfoque puro",
            "Silencia notificaciones durante el Pomodoro",
            "Cada hora: levántate y muévete 2 minutos",
        ),
        listOf(
            "Usa el método Pomodoro (25-5 min)",
            "Elimina distracciones del escritorio",
            "Escribe la tarea actual en un post-it",
            "Escucha música sin letras mientras trabajas",
        ),
    ),
    VoiceChallenge(
        "finish",
        "Inicio las cosas y no las termino",
        "✅",
        listOf(
            "Recordatorio: ¿Terminaste la tarea de hoy?",
            "Check-in a la mitad del día: ¿Cómo vas?",
            "Celebra cada tarea terminada con una TD-Coin",
        ),
        listOf(
            "Divide cada tarea en pasos de 10 min",
            "Comprométete solo con UNA tarea a la vez",
            "Usa la regla de los 2 minutos: si tarda poco, hazlo ya",
            "Registra tu progreso visualmente",
        ),
    ),
    VoiceChallenge(
        "organize",
        "Me cuesta organizarme",
        "🗂️",
        listOf(
            "Cada mañana: revisa tus 3 prioridades del día",
            "Recordatorio de planificación: Domingo 7 PM",
            "Alerta: no olvides tu lista de tareas",
        ),
        listOf(
            "Escribe tus tareas la noche anterior",
            "Usa colores para priorizar tareas",
            "Pon solo 3 tareas principales por día",
            "Revisa tu lista cada mañana al despertar",
        ),
    ),
    VoiceChallenge(
        "impulsive",
        "Soy muy impulsivo/a",
        "⚡",
        listOf(
            "Antes de actuar: respira profundo 5 veces",
            "Recordatorio: espera 10 min antes de decidir",
            "Check emocional: ¿Cómo te sientes ahora?",
        ),
        listOf(
            "Practica la pausa de 10 segundos",
            "Escribe antes de responder algo importante",
            "Identifica tus detonantes emocionales",
            "Celebra cada vez que pauses antes de reaccionar",
        ),
    ),
)*/