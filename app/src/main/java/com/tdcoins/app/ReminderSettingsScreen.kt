package com.tdcoins.app

import android.app.TimePickerDialog
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun ReminderSettingsScreen(
    settings: ReminderSettings,
    permissionGranted: Boolean,
    onSettingsChange: (ReminderSettings) -> Unit,
    onRequestPermission: (enableAfterGrant: Boolean) -> Unit,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    fun showTimePicker(hour: Int, onSelected: (Int, Int) -> Unit) {
        TimePickerDialog(context, { _, selectedHour, minute ->
            onSelected(selectedHour, minute)
        }, hour, if (hour == settings.hour) settings.minute else 0, true).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Column {
                Text("Recordatorios", fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text("Tú decides cuándo y qué recibir.", color = MutedText, fontSize = 13.sp)
            }
        }

        ScreenCard {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = PrimaryPurple)
                    Column {
                        Text("Recordatorio diario", fontWeight = FontWeight.Bold)
                        Text(
                            if (settings.enabled) "Activo" else "Desactivado",
                            color = MutedText,
                            fontSize = 12.sp,
                        )
                    }
                }
                Switch(
                    checked = settings.enabled,
                    onCheckedChange = { enabled ->
                        if (enabled && !permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            onRequestPermission(true)
                        } else {
                            onSettingsChange(settings.copy(enabled = enabled))
                        }
                    },
                )
            }
        }

        if (!permissionGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ScreenCard {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Permiso necesario", fontWeight = FontWeight.Bold)
                    Text(
                        "Android necesita tu permiso para mostrar recordatorios. No se enviarán avisos sin él.",
                        color = MutedText,
                        fontSize = 13.sp,
                    )
                    Button(onClick = { onRequestPermission(false) }) { Text("Permitir notificaciones") }
                }
            }
        }

        SectionLabel("Hora")
        ScreenCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        showTimePicker(settings.hour) { hour, minute ->
                            onSettingsChange(settings.copy(hour = hour, minute = minute))
                        }
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Enviar a esta hora", fontWeight = FontWeight.Bold)
                Text(formatTime(settings.hour, settings.minute), color = PrimaryPurple, fontWeight = FontWeight.Black)
            }
        }

        SectionLabel("Tipos de aviso")
        ScreenCard {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                ReminderType.entries.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val types = if (type in settings.types) settings.types - type else settings.types + type
                                if (types.isNotEmpty()) onSettingsChange(settings.copy(types = types))
                            }
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = type in settings.types,
                            onCheckedChange = {
                                val types = if (type in settings.types) settings.types - type else settings.types + type
                                if (types.isNotEmpty()) onSettingsChange(settings.copy(types = types))
                            },
                        )
                        Text(type.label, fontWeight = FontWeight.SemiBold)
                    }
                }
                Text(
                    "Debe quedar al menos un tipo seleccionado.",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = MutedText,
                    fontSize = 11.sp,
                )
            }
        }

        SectionLabel("Horario silencioso")
        ScreenCard {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "Si la hora elegida cae en este periodo, el aviso se mueve al final del horario silencioso.",
                    color = MutedText,
                    fontSize = 12.sp,
                )
                TimeSettingRow("Desde", settings.quietStartHour) {
                    showTimePicker(settings.quietStartHour) { hour, _ ->
                        onSettingsChange(settings.copy(quietStartHour = hour))
                    }
                }
                TimeSettingRow("Hasta", settings.quietEndHour) {
                    showTimePicker(settings.quietEndHour) { hour, _ ->
                        onSettingsChange(settings.copy(quietEndHour = hour))
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSettingRow(label: String, hour: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, fontWeight = FontWeight.Bold)
        Text(formatTime(hour, 0), color = PrimaryPurple, fontWeight = FontWeight.Bold)
    }
}

private fun formatTime(hour: Int, minute: Int): String =
    String.format(Locale.getDefault(), "%02d:%02d", hour, minute)