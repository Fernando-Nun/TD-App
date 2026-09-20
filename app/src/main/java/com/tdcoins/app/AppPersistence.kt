package com.tdcoins.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class AppPersistence(context: Context) {
    private val preferences = context.getSharedPreferences("td_coins_state", Context.MODE_PRIVATE)

    fun load(): AppSnapshot {
        val raw = preferences.getString(KEY_SNAPSHOT, null) ?: return AppSnapshot()
        return runCatching {
            val json = JSONObject(raw)
            AppSnapshot(
                coins = json.optInt("coins", 45),
                pomodorosDone = json.optInt("pomodorosDone", 0),
                missions = json.optJSONArray("missions").toMissions().ifEmpty { initialMissions() },
                purchasedIds = json.optJSONArray("purchasedIds").toStrings(),
                streakDays = json.optInt("streakDays", 0),
                lastActiveDate = json.optString("lastActiveDate", ""),
                voiceNotes = json.optJSONArray("voiceNotes").toStrings(),
            )
        }.getOrElse { AppSnapshot() }
    }

    fun save(snapshot: AppSnapshot) {
        val json = JSONObject()
            .put("coins", snapshot.coins)
            .put("pomodorosDone", snapshot.pomodorosDone)
            .put("streakDays", snapshot.streakDays)
            .put("lastActiveDate", snapshot.lastActiveDate)
            .put("purchasedIds", JSONArray(snapshot.purchasedIds))
            .put("voiceNotes", JSONArray(snapshot.voiceNotes))
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
                            .put("createdAt", mission.createdAt),
                    )
                }
            })
        preferences.edit().putString(KEY_SNAPSHOT, json.toString()).apply()
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
            )
        }
    }

    private companion object {
        const val KEY_SNAPSHOT = "snapshot"
    }
}