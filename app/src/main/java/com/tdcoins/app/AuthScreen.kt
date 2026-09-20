package com.tdcoins.app

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class AuthMode { SIGN_IN, REQUEST_RESET, CONFIRM_RESET }

@Composable
fun AuthScreen(
    loading: Boolean,
    error: String?,
    message: String?,
    onSubmit: (String, String, Boolean) -> Unit,
    onRequestReset: (String) -> Unit,
    onResetPassword: (String, String, String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var mode by remember { mutableStateOf(AuthMode.SIGN_IN) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .imePadding(),
    ) {
        MemphisCircle(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp)
                .size(150.dp),
            color = AccentPink,
            alpha = 0.12f,
        )
        MemphisCircle(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 8.dp, bottom = 40.dp)
                .size(110.dp),
            color = SecondaryTeal,
            alpha = 0.1f,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo),
                        contentDescription = "Logo de TD-App",
                        modifier = Modifier.size(62.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Column {
                        Text(
                            "TD-App",
                            color = Foreground,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                        )
                        Text(
                            "Pequeños pasos, progreso real",
                            color = MutedText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLavender),
                ) {
                    Text(
                        "ENFOQUE + HÁBITOS",
                        color = PrimaryPurple,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.7.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Foreground, Color(0xFF321451)),
                        ),
                    )
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                MemphisCircle(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 22.dp, y = (-28).dp)
                        .size(90.dp),
                    color = AccentOrange,
                    alpha = 0.2f,
                )
                Column {
                    Text(
                        "TU SIGUIENTE PEQUEÑO PASO",
                        color = Color(0xFFF9A8D4),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.1.sp,
                    )
                    Text(
                        when (mode) {
                            AuthMode.SIGN_IN -> "Vuelve a tu ritmo."
                            AuthMode.REQUEST_RESET -> "Recupera tu acceso."
                            AuthMode.CONFIRM_RESET -> "Crea una nueva clave."
                        },
                        color = Color.White,
                        fontSize = 27.sp,
                        lineHeight = 31.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        when (mode) {
                            AuthMode.SIGN_IN -> "Sincroniza tus avances y continúa donde lo dejaste."
                            AuthMode.REQUEST_RESET -> "Te ayudaremos a volver a entrar a tu cuenta."
                            AuthMode.CONFIRM_RESET -> "Escribe el código que recibiste y sigue adelante."
                        },
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 5.dp),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                AuthPill("Pomodoro", PrimaryPurple, Modifier.weight(1f))
                AuthPill("Misiones", AccentOrange, Modifier.weight(1f))
                AuthPill("Recompensas", SecondaryTeal, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLavender),
                shadowElevation = 4.dp,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        when (mode) {
                            AuthMode.SIGN_IN -> "Entra a tu cuenta"
                            AuthMode.REQUEST_RESET -> "Recupera tu cuenta"
                            AuthMode.CONFIRM_RESET -> "Confirma tu recuperación"
                        },
                        color = Foreground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                    )
                    Text(
                        when (mode) {
                            AuthMode.SIGN_IN -> "Tus datos quedan protegidos y sincronizados."
                            AuthMode.REQUEST_RESET -> "Usaremos tu correo para enviarte un código."
                            AuthMode.CONFIRM_RESET -> "El código caduca en 15 minutos y solo se puede usar una vez."
                        },
                        color = MutedText,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo") },
                        leadingIcon = {
                            Icon(Icons.Filled.MailOutline, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                    )

                    if (mode == AuthMode.CONFIRM_RESET) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it.uppercase() },
                            label = { Text("Código de recuperación") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }

                    if (mode != AuthMode.REQUEST_RESET) {
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = {
                                Text(if (mode == AuthMode.CONFIRM_RESET) "Nueva contraseña" else "Contraseña")
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Lock, contentDescription = null)
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }

                    error?.let {
                        AuthFeedback(
                            text = it,
                            isError = true,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                    message?.let {
                        AuthFeedback(
                            text = it,
                            isError = false,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }

                    when (mode) {
                        AuthMode.SIGN_IN -> {
                            Button(
                                onClick = { onSubmit(email.trim(), password, false) },
                                enabled = !loading && email.isNotBlank() && password.length >= 8,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            ) {
                                AuthButtonContent(if (loading) "Conectando…" else "Iniciar sesión", loading)
                            }
                            OutlinedButton(
                                onClick = { onSubmit(email.trim(), password, true) },
                                enabled = !loading && email.isNotBlank() && password.length >= 8,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text("Crear cuenta", fontWeight = FontWeight.Bold)
                            }
                            TextButton(
                                onClick = { mode = AuthMode.REQUEST_RESET },
                                enabled = !loading,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Text("Olvidé mi contraseña", color = MutedText, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        AuthMode.REQUEST_RESET -> {
                            Button(
                                onClick = {
                                    onRequestReset(email.trim())
                                    mode = AuthMode.CONFIRM_RESET
                                },
                                enabled = !loading && email.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            ) {
                                AuthButtonContent(if (loading) "Enviando…" else "Enviar código", loading)
                            }
                            OutlinedButton(
                                onClick = { mode = AuthMode.SIGN_IN },
                                enabled = !loading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text("Volver a iniciar sesión", fontWeight = FontWeight.Bold)
                            }
                        }

                        AuthMode.CONFIRM_RESET -> {
                            Button(
                                onClick = { onResetPassword(email.trim(), code.trim(), password) },
                                enabled = !loading && email.isNotBlank() && code.isNotBlank() && password.length >= 8,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .height(54.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                            ) {
                                AuthButtonContent(if (loading) "Actualizando…" else "Cambiar contraseña", loading)
                            }
                            OutlinedButton(
                                onClick = {
                                    password = ""
                                    code = ""
                                    mode = AuthMode.SIGN_IN
                                },
                                enabled = !loading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .height(52.dp),
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text("Volver a iniciar sesión", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 14.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = SecondaryTeal,
                    modifier = Modifier.size(15.dp),
                )
                Text(
                    "Tu progreso se guarda para que puedas continuar en cualquier dispositivo.",
                    color = MutedText,
                    fontSize = 10.sp,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun AuthPill(label: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.78f))
            .border(1.dp, color.copy(alpha = 0.28f), RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(50))
                .background(color),
        )
        Text(
            label,
            color = MutedText,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun AuthFeedback(text: String, isError: Boolean, modifier: Modifier = Modifier) {
    val color = if (isError) Color(0xFFB42318) else Color(0xFF087F5B)
    val background = if (isError) Color(0xFFFFF1F2) else Color(0xFFECFDF5)
    val icon = if (isError) Icons.Filled.ErrorOutline else Icons.Filled.CheckCircle
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.18f)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Text(text, color = color, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun AuthButtonContent(label: String, loading: Boolean) {
    if (loading) {
        CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = Color.White,
            strokeWidth = 2.dp,
        )
    } else {
        Text(label, fontWeight = FontWeight.Black)
        Icon(
            Icons.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 8.dp)
                .size(18.dp),
        )
    }
}