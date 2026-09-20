package com.tdcoins.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class AppPersistence(context: Context) {
    private val preferences = context.getSharedPreferences("td_coins_state", Context.MODE_PRIVATE)

    fun load(): AppSnapshot {
        val raw = preferences.getString(scoped(KEY_SNAPSHOT), null)
            ?: claimLegacySnapshot()
            ?: return AppSnapshot()
        return runCatching {
            fromJson(JSONObject(raw))
        }.getOrElse { AppSnapshot() }
    }

    fun fromJson(json: JSONObject): AppSnapshot =
        AppSnapshot(
                coins = json.optInt("coins", 45),
                pomodorosDone = json.optInt("pomodorosDone", 0),
                pomodoroBaseline = json.optInt(
                    "pomodoroBaseline",
                    (json.optInt("pomodorosDone", 0) -
                        json.optJSONArray("economyEvents").toEconomyEvents().count {
                            it.id.startsWith("pomodoro-")
                        }).coerceAtLeast(0),
                ),
                missions = json.optJSONArray("missions").toMissions()
                    .filter { it.id !in LEGACY_MISSION_IDS },
                deletedMissionIds = (json.optJSONArray("deletedMissionIds").toStrings() + LEGACY_MISSION_IDS).distinct(),
                challenges = json.optJSONArray("challenges").toChallenges(),
                deletedChallengeIds = json.optJSONArray("deletedChallengeIds").toStrings(),
                purchasedIds = json.optJSONArray("purchasedIds").toStrings(),
                streakDays = json.optInt("streakDays", 0),
                lastActiveDate = json.optString("lastActiveDate", ""),
                voiceNotes = json.optJSONArray("voiceNotes").toStrings(),
                economyEvents = json.optJSONArray("economyEvents").toEconomyEvents(),
            )

    fun save(snapshot: AppSnapshot) {
        preferences.edit().putString(scoped(KEY_SNAPSHOT), toJson(snapshot).toString()).apply()
    }

    fun toJson(snapshot: AppSnapshot): JSONObject =
        JSONObject()
            .put("coins", snapshot.coins)
            .put("pomodorosDone", snapshot.pomodorosDone)
            .put("pomodoroBaseline", snapshot.pomodoroBaseline)
            .put("streakDays", snapshot.streakDays)
            .put("lastActiveDate", snapshot.lastActiveDate)
            .put("purchasedIds", JSONArray(snapshot.purchasedIds))
            .put("voiceNotes", JSONArray(snapshot.voiceNotes))
            .put("economyEvents", JSONArray().apply {
                snapshot.economyEvents.forEach {
                    put(JSONObject().put("id", it.id).put("delta", it.delta).put("createdAt", it.createdAt))
                }
            })
            .put("deletedMissionIds", JSONArray(snapshot.deletedMissionIds))
            .put("deletedChallengeIds", JSONArray(snapshot.deletedChallengeIds))
            .put("challenges", JSONArray().apply {
                snapshot.challenges.forEach { challenge ->
                    put(JSONObject()
                        .put("id", challenge.id)
                        .put("text", challenge.text)
                        .put("icon", challenge.icon)
                        .put("reminders", JSONArray(challenge.reminders))
                        .put("plan", JSONArray(challenge.plan))
                        .put("createdAt", challenge.createdAt)
                        .put("updatedAt", challenge.updatedAt))
                }
            })
            .put("missions", JSONArray().apply {
                snapshot.missions.forEach { mission ->
                    put(
                        JSONObject()
                            .put("id", mission.id)
                            .put("title", mission.title)
                            .put("category", mission.category.name)
                            .put("target", mission.target)
                            .put("progress", mission.progress)
                            .put("coins", mission.coins)
                            .put("completed", mission.completed)
                            .put("createdAt", mission.createdAt)
                            .put("updatedAt", mission.updatedAt),
                    )
                }
            })

    fun sessionToken(): String? = preferences.getString(KEY_SESSION, null)
    fun saveSession(token: String?, userId: String? = null) = preferences.edit().run {
        if (token == null) {
            remove(KEY_SESSION)
            remove(KEY_ACTIVE_USER)
        } else {
            require(!userId.isNullOrBlank())
            putString(KEY_SESSION, token)
            putString(KEY_ACTIVE_USER, userId)
        }
    }.apply()
    fun deviceId(): String = preferences.getString(KEY_DEVICE, null)
        ?: java.util.UUID.randomUUID().toString().also { preferences.edit().putString(KEY_DEVICE, it).apply() }
    fun pendingOperation(): String = preferences.getString(scoped(KEY_PENDING), null)
        ?: java.util.UUID.randomUUID().toString().also {
            preferences.edit().putString(scoped(KEY_PENDING), it).apply()
        }
    fun acknowledgeOperation() = preferences.edit().remove(scoped(KEY_PENDING)).apply()

    private fun scoped(key: String): String {
        val userId = preferences.getString(KEY_ACTIVE_USER, null) ?: "signed-out"
        return "$key:$userId"
    }

    private fun claimLegacySnapshot(): String? {
        if (preferences.getBoolean(KEY_LEGACY_CLAIMED, false)) return null
        val legacy = preferences.getString(KEY_SNAPSHOT, null) ?: return null
        preferences.edit()
            .putBoolean(KEY_LEGACY_CLAIMED, true)
            .putString(scoped(KEY_SNAPSHOT), legacy)
            .remove(KEY_SNAPSHOT)
            .apply()
        return legacy
    }

    private fun JSONArray?.toStrings(): List<String> =
        if (this == null) emptyList() else List(length()) { index -> optString(index) }

    private fun JSONArray?.toMissions(): List<Mission> {
        if (this == null) return emptyList()
        return List(length()) { index ->
            val item = getJSONObject(index)
            Mission(
                id = item.getString("id"),
                title = item.getString("title"),
                category = runCatching {
                    MissionCategory.valueOf(item.getString("category"))
                }.getOrDefault(MissionCategory.FOCUS),
                target = item.optInt("target", 1).coerceAtLeast(1),
                progress = item.optInt("progress", 0),
                coins = item.optInt("coins", 20),
                completed = item.optBoolean("completed", false),
                createdAt = item.optLong("createdAt", System.currentTimeMillis()),
                updatedAt = item.optLong("updatedAt", item.optLong("createdAt", System.currentTimeMillis())),
            )
        }
    }

    private fun JSONArray?.toEconomyEvents(): List<EconomyEvent> =
        if (this == null) emptyList() else List(length()) { index ->
            getJSONObject(index).let {
                EconomyEvent(it.getString("id"), it.getInt("delta"), it.optLong("createdAt", 0))
            }
        }

    private fun JSONArray?.toChallenges(): List<VoiceChallenge> =
        if (this == null) emptyList() else List(length()) { index ->
            getJSONObject(index).let {
                VoiceChallenge(
                    id = it.getString("id"),
                    text = it.optString("text"),
                    icon = it.optString("icon", "🎯"),
                    reminders = it.optJSONArray("reminders").toStrings(),
                    plan = it.optJSONArray("plan").toStrings(),
                    createdAt = it.optLong("createdAt", System.currentTimeMillis()),
                    updatedAt = it.optLong("updatedAt", System.currentTimeMillis()),
                )
            }
        }

    private companion object {
        const val KEY_SNAPSHOT = "snapshot"
        const val KEY_SESSION = "session"
        const val KEY_ACTIVE_USER = "active_user"
        const val KEY_LEGACY_CLAIMED = "legacy_claimed"
        const val KEY_DEVICE = "device"
        const val KEY_PENDING = "pending_operation"
        val LEGACY_MISSION_IDS = setOf("t1", "t2", "t3", "t4", "t5")
    }
}