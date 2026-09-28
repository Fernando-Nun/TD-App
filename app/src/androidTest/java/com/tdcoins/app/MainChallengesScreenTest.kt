package com.tdcoins.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MainChallengesScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun deletesMainChallengeWithAccessibleButtonAndConfirmation() {
        val challenge = VoiceChallenge(
            id = "challenge-1",
            text = "Organizar mis estudios",
            icon = "target",
            reminders = emptyList(),
            plan = emptyList(),
        )
        var challenges by mutableStateOf(listOf(challenge))

        composeRule.setContent {
            TDCoinsTheme {
                VoiceScreen(
                    savedNotes = emptyList(),
                    onSaveNote = {},
                    onCreateMission = {},
                    challenges = challenges,
                    onChallengeDeleted = { id ->
                        challenges = challenges.filterNot { it.id == id }
                    },
                )
            }
        }

        composeRule
            .onNodeWithContentDescription("Eliminar reto principal Organizar mis estudios")
            .performClick()
        composeRule.onNodeWithText("¿Eliminar reto principal?").assertExists()

        composeRule.onNodeWithText("Cancelar").performClick()
        composeRule.onNodeWithText("Organizar mis estudios").assertExists()

        composeRule
            .onNodeWithContentDescription("Eliminar reto principal Organizar mis estudios")
            .performClick()
        composeRule.onNodeWithText("Eliminar").performClick()
        composeRule.onNodeWithText("¿Eliminar reto principal?").assertDoesNotExist()
        composeRule.onNodeWithText("Organizar mis estudios").assertDoesNotExist()
        composeRule.runOnIdle { assertEquals(emptyList<VoiceChallenge>(), challenges) }
    }
}