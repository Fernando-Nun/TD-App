package com.tdcoins.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp

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
    Column(
        modifier = Modifier.fillMaxSize().padding(28.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            when (mode) {
                AuthMode.SIGN_IN -> "Sincroniza tus TD-Coins"
                AuthMode.REQUEST_RESET -> "Recupera tu cuenta"
                AuthMode.CONFIRM_RESET -> "Escribe tu código"
            },
        )
        Text(
            when (mode) {
                AuthMode.SIGN_IN -> "Inicia sesión para mantener tu progreso en todos tus dispositivos."
                AuthMode.REQUEST_RESET -> "Te enviaremos un código si existe una cuenta con ese correo."
                AuthMode.CONFIRM_RESET -> "El código caduca en 15 minutos y solo se puede usar una vez."
            },
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
        )
        if (mode == AuthMode.CONFIRM_RESET) {
            OutlinedTextField(
                value = code,
                onValueChange = { code = it.uppercase() },
                label = { Text("Código de recuperación") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
        if (mode != AuthMode.REQUEST_RESET) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(if (mode == AuthMode.CONFIRM_RESET) "Nueva contraseña" else "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
        error?.let { Text(it, color = androidx.compose.material3.MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp)) }
        message?.let { Text(it, modifier = Modifier.padding(top = 12.dp)) }
        when (mode) {
            AuthMode.SIGN_IN -> {
                Button(
                    onClick = { onSubmit(email, password, false) },
                    enabled = !loading && email.isNotBlank() && password.length >= 8,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                ) { Text(if (loading) "Conectando…" else "Iniciar sesión") }
                OutlinedButton(
                    onClick = { onSubmit(email, password, true) },
                    enabled = !loading && email.isNotBlank() && password.length >= 8,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) { Text("Crear cuenta") }
                OutlinedButton(
                    onClick = { mode = AuthMode.REQUEST_RESET },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) { Text("Olvidé mi contraseña") }
            }
            AuthMode.REQUEST_RESET -> {
                Button(
                    onClick = {
                        onRequestReset(email)
                        mode = AuthMode.CONFIRM_RESET
                    },
                    enabled = !loading && email.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                ) { Text(if (loading) "Enviando…" else "Enviar código") }
                OutlinedButton(
                    onClick = { mode = AuthMode.SIGN_IN },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) { Text("Volver") }
            }
            AuthMode.CONFIRM_RESET -> {
                Button(
                    onClick = { onResetPassword(email, code, password) },
                    enabled = !loading && email.isNotBlank() && code.isNotBlank() && password.length >= 8,
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                ) { Text(if (loading) "Actualizando…" else "Cambiar contraseña") }
                OutlinedButton(
                    onClick = {
                        password = ""
                        code = ""
                        mode = AuthMode.SIGN_IN
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                ) { Text("Volver a iniciar sesión") }
            }
        }
    }
}