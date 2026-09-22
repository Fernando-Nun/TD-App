package com.tdcoins.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun VoiceScreen(
    savedNotes: List<String>,
    onSaveNote: (String) -> Unit,
    onCreateMission: (String) -> Unit,
    onNotesCleared: () -> Unit = {},
    challenges: List<VoiceChallenge> = emptyList(),
    onChallengesChange: (List<VoiceChallenge>) -> Unit = {},
    onChallengeDeleted: (String) -> Unit = {},
    onGenerateChallenge: suspend (String) -> Result<VoiceChallenge> = {
        Result.success(createPersonalChallenge(it))
    },
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var recording by remember { mutableStateOf(false) }
    var transcript by remember { mutableStateOf("") }
    var recognitionMessage by remember { mutableStateOf("Toca el micrófono y describe lo que necesitas lograr.") }
    var selectedIds by remember { mutableStateOf(emptyList<String>()) }
    var showPlan by remember { mutableStateOf(false) }
    var showAddChallenge by remember { mutableStateOf(false) }
    var showClearNotes by remember { mutableStateOf(false) }
    var generatingPlan by remember { mutableStateOf(false) }
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    DisposableEffect(speechRecognizer) {
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                recognitionMessage = "Escuchando… habla con claridad."
            }
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() {
                recording = false
                recognitionMessage = "Procesando transcripción…"
            }
            override fun onError(error: Int) {
                recording = false
                recognitionMessage = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "No pude entenderte. Intenta hablar un poco más despacio."
                    SpeechRecognizer.ERROR_NETWORK,
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "No hay conexión para transcribir. Inténtalo de nuevo."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Activa el permiso del micrófono para usar esta función."
                    else -> "No fue posible transcribir. Puedes escribir la nota manualmente."
                }
            }
            override fun onResults(results: Bundle?) {
                recording = false
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                transcript = matches?.firstOrNull().orEmpty()
                recognitionMessage = if (transcript.isBlank()) {
                    "No se detectó texto. Puedes escribirlo manualmente."
                } else {
                    "Transcripción lista. Revísala antes de guardarla."
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.firstOrNull()
                    ?.takeIf { it.isNotBlank() }
                    ?.let { transcript = it }
            }
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })
        onDispose { speechRecognizer?.destroy() }
    }

    fun beginRecognition() {
        if (speechRecognizer == null) {
            recognitionMessage = "Este dispositivo no tiene un servicio de reconocimiento de voz."
            return
        }
        transcript = ""
        recording = true
        speechRecognizer.startListening(
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale("es", "MX").toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Describe tu reto")
            },
        )
    }

    val microphonePermission = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) beginRecognition()
        else recognitionMessage = "Sin permiso de micrófono puedes escribir tu nota manualmente."
    }

    fun toggleRecording() {
        if (recording) {
            speechRecognizer?.stopListening()
            recording = false
        } else if (
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            beginRecognition()
        } else {
            microphonePermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    if (showPlan && selectedIds.isNotEmpty()) {
        PersonalizedPlan(
            challenges = challenges.filter { it.id in selectedIds },
            onBack = { showPlan = false },
        )
        return
    }

    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Personaliza tu App", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("Cuéntame tus retos o elige los que aplican", color = MutedText, fontSize = 13.sp, modifier = Modifier.padding(top = 2.dp))
        }
        item {
            ScreenCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("Presiona y habla sobre tus dificultades", color = MutedText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Surface(
                        onClick = { toggleRecording() },
                        modifier = Modifier
                            .padding(top = 14.dp)
                            .size(80.dp)
                            .testTag("voice-record"),
                        shape = CircleShape,
                        color = if (recording) Color(0xFFEF4444) else PrimaryPurple,
                        shadowElevation = 8.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                if (recording) Icons.Filled.Stop else Icons.Filled.Mic,
                                contentDescription = if (recording) "Detener grabación" else "Iniciar grabación",
                                tint = Color.White,
                                modifier = Modifier.size(34.dp),
                            )
                        }
                    }
                    if (recording) {
                        Row(
                            modifier = Modifier
                                .height(42.dp)
                                .padding(top = 10.dp),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                        ) {
                            listOf(15, 28, 20, 32, 18).forEach { barHeight ->
                                Box(
                                    modifier = Modifier
                                        .size(width = 7.dp, height = barHeight.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryPurple),
                                )
                            }
                        }
                        Text("Escuchando...", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text(recognitionMessage, color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(top = 10.dp))
                    }
                    OutlinedTextField(
                        value = transcript,
                        onValueChange = { transcript = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        label = { Text("Nota transcrita o escrita") },
                        placeholder = { Text("Ejemplo: terminar mi proyecto antes del viernes") },
                        minLines = 2,
                    )
                    if (transcript.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            OutlinedButton(
                                onClick = {
                                    onSaveNote(transcript.trim())
                                    recognitionMessage = "Nota guardada en este dispositivo y en su copia de seguridad."
                                },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("Guardar nota")
                            }
                            Button(
                                onClick = { onCreateMission(transcript.trim()) },
                                modifier = Modifier.weight(1f),
                            ) {
                                Text("Crear misión")
                            }
                        }
                    }
                }
            }
        }
        if (savedNotes.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SectionLabel("Notas recientes")
                    TextButton(onClick = { showClearNotes = true }) {
                        Icon(
                            Icons.Filled.DeleteOutline,
                            contentDescription = "Borrar notas recientes",
                            tint = Color(0xFFDC2626),
                        )
                        Text(
                            "Borrar",
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(start = 4.dp),
                        )
                    }
                }
            }
            items(savedNotes.take(3)) { note ->
                ScreenCard {
                    Text(
                        note,
                        modifier = Modifier.padding(14.dp),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                    )
                }
            }

        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SectionLabel("Mis retos principales")
                Button(onClick = { showAddChallenge = true }) { Text("Agregar") }
            }
        }
        if (challenges.isEmpty()) {
            item {
                ScreenCard {
                    Text("Aún no tienes retos principales. Agrega uno para recibir un plan hecho para tu objetivo.", modifier = Modifier.padding(16.dp), color = MutedText, fontSize = 13.sp)
                }
            }
        }
        items(challenges, key = { it.id }) { challenge ->
            val selected = challenge.id in selectedIds
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (selected) MutedLavender else Color.White)
                    .border(2.dp, if (selected) PrimaryPurple else BorderLavender, RoundedCornerShape(16.dp))
                    .clickable {
                        selectedIds = if (selected) selectedIds - challenge.id else selectedIds + challenge.id
                    }
                    .semantics {
                        role = Role.Checkbox
                        this.selected = selected
                        contentDescription = challenge.text
                    }
                    .testTag("challenge-${challenge.id}")
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(challenge.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Plan personalizado listo", color = MutedText, fontSize = 10.sp)
                }
                TextButton(
                    onClick = {
                        selectedIds = selectedIds - challenge.id
                        onChallengeDeleted(challenge.id)
                    },
                ) {
                    Text("Quitar", color = Color(0xFFB91C1C))
                }
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (selected) PrimaryPurple else Color.Transparent)
                        .border(2.dp, if (selected) PrimaryPurple else BorderLavender, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selected) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
        if (selectedIds.isNotEmpty()) {
            item {
                Button(
                    onClick = { showPlan = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Ver mi Plan Personalizado (${selectedIds.size} retos) →", fontWeight = FontWeight.Black)
                }
            }
        }
        item { Spacer(modifier = Modifier.height(4.dp)) }
    }
    if (showClearNotes) {
        AlertDialog(
            onDismissRequest = { showClearNotes = false },
            title = { Text("¿Borrar notas recientes?") },
            text = { Text("Se eliminarán de este dispositivo y no volverán a aparecer al sincronizar.") },
            confirmButton = {
                Button(
                    onClick = {
                        onNotesCleared()
                        showClearNotes = false
                    },
                ) {
                    Text("Borrar notas")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearNotes = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
    if (showAddChallenge) {
        AddChallengeDialog(
            generating = generatingPlan,
            onDismiss = { if (!generatingPlan) showAddChallenge = false },
            onAdd = { text ->
                scope.launch {
                    generatingPlan = true
                    onGenerateChallenge(text)
                        .onSuccess { generated ->
                            onChallengesChange(listOf(generated) + challenges)
                            recognitionMessage = "Reto creado con un plan personalizado."
                            showAddChallenge = false
                        }
                        .onFailure {
                            onChallengesChange(listOf(createPersonalChallenge(text)) + challenges)
                            recognitionMessage = "Gemini está ocupado. Se creó un plan provisional adaptado a tu texto; puedes volver a intentarlo más tarde."
                            showAddChallenge = false
                        }
                    generatingPlan = false
                }
            },
        )
    }
}

@Composable
private fun AddChallengeDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    generating: Boolean,
) {
    var text by remember { mutableStateOf("") }
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo reto principal", fontWeight = FontWeight.Black) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("¿Qué quieres mejorar?") },
                placeholder = { Text("Ejemplo: me cuesta organizar mis estudios") },
                minLines = 2,
            )
        },
        confirmButton = {
            Button(
                onClick = { if (text.isNotBlank()) onAdd(text.trim()) },
                enabled = text.isNotBlank() && !generating,
            ) {
                if (generating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text("Crear plan")
                }
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(
                onClick = onDismiss,
                enabled = !generating,
            ) {
                Text("Cancelar")
            }
        },
    )
}

@Composable
private fun PersonalizedPlan(
    challenges: List<VoiceChallenge>,
    onBack: () -> Unit,
) {
    LazyColumn(
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MutedLavender),
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", modifier = Modifier.size(19.dp))
                }
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Tu Plan Personalizado", fontSize = 20.sp, fontWeight = FontWeight.Black)
                    Text("Adaptado a partir de tus desafíos", color = MutedText, fontSize = 12.sp)
                }
            }
        }
        items(challenges, key = { it.id }) { challenge ->
            ScreenCard {
                Column(modifier = Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(challenge.text, fontSize = 14.sp, fontWeight = FontWeight.Black, lineHeight = 19.sp)
                    }
                    Text(
                        "RECORDATORIOS ACTIVADOS",
                        color = PrimaryPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 14.dp, bottom = 5.dp),
                    )
                    challenge.reminders.forEach { reminder ->
                        Text("• $reminder", fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(vertical = 3.dp))
                    }
                    Text(
                        "PLAN DE ACCIÓN",
                        color = SecondaryTeal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 12.dp, bottom = 5.dp),
                    )
                    challenge.plan.forEachIndexed { index, action ->
                        Text("${index + 1}. $action", fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.padding(vertical = 3.dp))
                    }
                }
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.linearGradient(listOf(SecondaryTeal, Color(0xFF0D9488))))
                    .padding(15.dp),
            ) {
                Text("Recuerda", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text(
                    "El TDAH es una diferencia, no un defecto. Con las herramientas correctas, puedes lograr todo lo que te propones. ¡Vamos!",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
        item { Spacer(modifier = Modifier.height(4.dp)) }
    }
}