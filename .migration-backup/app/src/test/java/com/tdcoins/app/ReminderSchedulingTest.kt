package com.tdcoins.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime

class ReminderSchedulingTest {
    private val zone = ZoneId.of("America/Mexico_City")

    @Test
    fun `overnight quiet hours include late night and early morning`() {
        assertTrue(AppNotifications.isQuietHour(23, 22, 7))
        assertTrue(AppNotifications.isQuietHour(6, 22, 7))
        assertFalse(AppNotifications.isQuietHour(12, 22, 7))
    }

    @Test
    fun `reminder during quiet hours moves to quiet period end`() {
        val now = ZonedDateTime.of(2026, 9, 20, 20, 0, 0, 0, zone)
        val settings = ReminderSettings(hour = 23, minute = 30, quietStartHour = 22, quietEndHour = 7)

        val result = ZonedDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(AppNotifications.nextReminderMillis(settings, now)),
            zone,
        )

        assertEquals(ZonedDateTime.of(2026, 9, 21, 7, 0, 0, 0, zone), result)
    }

    @Test
    fun `past reminder time schedules the following day`() {
        val now = ZonedDateTime.of(2026, 9, 20, 10, 0, 0, 0, zone)
        val settings = ReminderSettings(hour = 9, minute = 15)

        val result = ZonedDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(AppNotifications.nextReminderMillis(settings, now)),
            zone,
        )

        assertEquals(ZonedDateTime.of(2026, 9, 21, 9, 15, 0, 0, zone), result)
    }

    @Test
    fun `delayed delivery in quiet hours moves to the next quiet period end`() {
        val now = ZonedDateTime.of(2026, 9, 20, 23, 45, 0, 0, zone)
        val settings = ReminderSettings(quietStartHour = 22, quietEndHour = 7)

        val result = ZonedDateTime.ofInstant(
            java.time.Instant.ofEpochMilli(AppNotifications.nextQuietEndMillis(settings, now)),
            zone,
        )

        assertEquals(ZonedDateTime.of(2026, 9, 21, 7, 0, 0, 0, zone), result)
    }

    @Test
    fun `granting permission alone keeps disabled reminders disabled`() {
        val settings = ReminderSettings(enabled = false)

        val result = settingsAfterPermissionResult(
            settings,
            granted = true,
            enableAfterGrant = false,
        )

        assertFalse(result.enabled)
    }

    @Test
    fun `granting permission from the switch enables reminders`() {
        val settings = ReminderSettings(enabled = false)

        val result = settingsAfterPermissionResult(
            settings,
            granted = true,
            enableAfterGrant = true,
        )

        assertTrue(result.enabled)
    }
}