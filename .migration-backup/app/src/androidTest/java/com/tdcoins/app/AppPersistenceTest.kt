package com.tdcoins.app

import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AppPersistenceTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val persistence = AppPersistence(context)

    @Before
    @After
    fun clearStoredState() {
        context.getSharedPreferences("td_coins_state", android.content.Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun savesAndRestoresMissionProgressAndVoiceNotes() {
        val mission = Mission(
            id = "persisted",
            title = "Misión persistida",
            category = MissionCategory.HABITS,
            target = 4,
            progress = 3,
            coins = 50,
        )
        persistence.save(
            AppSnapshot(
                coins = 95,
                missions = listOf(mission),
                voiceNotes = listOf("Nota persistida"),
            ),
        )

        val restored = AppPersistence(context).load()

        assertEquals(95, restored.coins)
        assertEquals(mission, restored.missions.single())
        assertEquals(listOf("Nota persistida"), restored.voiceNotes)
    }
}