package com.tdcoins.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.net.URL
import javax.net.ssl.SSLException

class SyncClient(private val persistence: AppPersistence) {
    suspend fun authenticate(email: String, password: String, register: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = request(
                    "/api/auth/${if (register) "register" else "login"}",
                    JSONObject().put("email", email).put("password", password),
                    null,
                )
                persistence.saveSession(response.getString("token"), response.getString("userId"))
            }
        }

    suspend fun requestPasswordReset(email: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            request(
                "/api/auth/password-reset/request",
                JSONObject().put("email", email),
                null,
            ).getString("message")
        }
    }

    suspend fun resetPassword(email: String, code: String, password: String): Result<String> =
        withContext(Dispatchers.IO) {
            runCatching {
                request(
                    "/api/auth/password-reset/confirm",
                    JSONObject().put("email", email).put("code", code).put("password", password),
                    null,
                ).getString("message")
            }
        }

    suspend fun sync(snapshot: AppSnapshot): Result<AppSnapshot> = withContext(Dispatchers.IO) {
        runCatching {
            val token = persistence.sessionToken() ?: error("No active session")
            val operationId = persistence.pendingOperation()
            val response = request(
                "/api/sync",
                JSONObject()
                    .put("operationId", operationId)
                    .put("deviceId", persistence.deviceId())
                    .put("snapshot", persistence.toJson(snapshot)),
                token,
            )
            persistence.fromJson(response.getJSONObject("snapshot")).also {
                persistence.acknowledgeOperation()
            }
        }
    }

    fun signOut() = persistence.saveSession(null)

    private fun request(path: String, body: JSONObject, token: String?): JSONObject {
        check(BuildConfig.SYNC_API_URL.isNotBlank()) { "El servidor de sincronización no está configurado." }
        val connection = URL(BuildConfig.SYNC_API_URL.trimEnd('/') + path).openConnection() as HttpURLConnection
        return try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            token?.let { connection.setRequestProperty("Authorization", "Bearer $it") }
            connection.connectTimeout = 10_000
            connection.readTimeout = 10_000
            connection.doOutput = true
            connection.outputStream.use { it.write(body.toString().toByteArray()) }
            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
            val response = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            val json = if (response.isBlank()) {
                JSONObject()
            } else {
                runCatching { JSONObject(response) }.getOrElse {
                    error(
                        if (responseCode in 200..299) {
                            "El servidor devolvió una respuesta no válida. Descarga la versión más reciente de TD-App."
                        } else {
                            "No se pudo conectar con el servidor de TD-App (HTTP $responseCode)."
                        },
                    )
                }
            }
            if (responseCode !in 200..299) error(json.optString("error", "Error de conexión"))
            json
        } catch (error: IOException) {
            throw IOException(
                when (error) {
                    is UnknownHostException ->
                        "No se encontró el servidor de TD-App. Comprueba que el teléfono tenga internet."
                    is SocketTimeoutException ->
                        "El servidor tardó demasiado en responder. Comprueba tu conexión e inténtalo de nuevo."
                    is SSLException ->
                        "No se pudo establecer una conexión segura con TD-App."
                    is ConnectException ->
                        "No se pudo conectar con el servidor de TD-App. Comprueba tu conexión."
                    else ->
                        "No se pudo conectar con el servidor de TD-App. Comprueba tu conexión."
                },
                error,
            )
        } finally {
            connection.disconnect()
        }
    }
}