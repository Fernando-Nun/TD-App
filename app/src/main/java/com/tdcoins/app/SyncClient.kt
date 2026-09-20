package com.tdcoins.app

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

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
            val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
            val response = stream.bufferedReader().use { it.readText() }
            val json = if (response.isBlank()) JSONObject() else JSONObject(response)
            if (connection.responseCode !in 200..299) error(json.optString("error", "Error de conexión"))
            json
        } finally {
            connection.disconnect()
        }
    }
}