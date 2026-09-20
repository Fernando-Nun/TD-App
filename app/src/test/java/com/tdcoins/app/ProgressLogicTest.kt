package com.tdcoins.app

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressLogicTest {
    private val today = LocalDate.of(2026, 9, 19)

    @Test
    fun `first activity starts a one day streak`() {
        assertEquals(StreakResult(1, "2026-09-19"), updateStreak(0, "", today))
    }

    @Test
    fun `two activities on the same day do not duplicate the streak`() {
        assertEquals(StreakResult(4, "2026-09-19"), updateStreak(4, "2026-09-19", today))
    }

    @Test
    fun `activity on the next day increases the streak`() {
        assertEquals(StreakResult(5, "2026-09-19"), updateStreak(4, "2026-09-18", today))
    }

    @Test
    fun `a missed day restarts the streak`() {
        assertEquals(StreakResult(1, "2026-09-19"), updateStreak(8, "2026-09-16", today))
    }

    @Test
    fun `an invalid saved date safely restarts the streak`() {
        assertEquals(StreakResult(1, "2026-09-19"), updateStreak(8, "fecha-invalida", today))
    }

    @Test
    fun `mission rewards are automatic and capped`() {
        val shortReward = calculateMissionReward("Ordenar escritorio", MissionCategory.ORDER, 1)
        val largeReward = calculateMissionReward(
            "proyecto examen ejercicio entrenar estudiar limpiar organizar",
            MissionCategory.FOCUS,
            30,
        )

        assertTrue(shortReward in 15..150)
        assertEquals(150, largeReward)
    }

    @Test
    fun `legacy and deleted missions never return after merge`() {
        val legacy = Mission("t1", "Misión predeterminada", MissionCategory.FOCUS, 1, 0, 20)
        val deleted = Mission("user-mission", "Misión eliminada", MissionCategory.HEALTH, 5, 3, 35)
        val merged = mergeSnapshots(
            AppSnapshot(deletedMissionIds = listOf(deleted.id)),
            AppSnapshot(missions = listOf(legacy, deleted)),
        )

        assertTrue(merged.missions.isEmpty())
        assertTrue(legacy.id in merged.deletedMissionIds)
        assertTrue(deleted.id in merged.deletedMissionIds)
    }
}