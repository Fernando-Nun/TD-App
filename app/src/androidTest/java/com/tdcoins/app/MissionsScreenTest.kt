package com.tdcoins.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MissionsScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun createsMissionFromDialog() {
        var missions by mutableStateOf(emptyList<Mission>())

        composeRule.setContent {
            TDCoinsTheme {
                MissionsScreen(
                    missions = missions,
                    onMissionsChange = { missions = it },
                    onReward = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Agregar misión").performClick()
        composeRule.onNodeWithText("Agregar Misión").assertIsNotEnabled()
        composeRule.onNodeWithText("¿Qué quieres lograr?").performTextReplacement("Preparar exposición")
        composeRule.onNodeWithText("Agregar Misión").assertIsEnabled().performClick()

        composeRule.onNodeWithText("Preparar exposición").assertExists()
        composeRule.runOnIdle {
            assertEquals("Preparar exposición", missions.single().title)
            assertEquals(5, missions.single().target)
        }
    }

    @Test
    fun advancesAndCompletesMissionOnlyOnce() {
        val original = Mission("test", "Entregar tarea", MissionCategory.FOCUS, 2, 1, 30)
        var missions by mutableStateOf(listOf(original))
        var rewards = 0

        composeRule.setContent {
            TDCoinsTheme {
                MissionsScreen(
                    missions = missions,
                    onMissionsChange = { missions = it },
                    onReward = { rewards += 1 },
                )
            }
        }

        composeRule.onNodeWithContentDescription("Avanzar misión Entregar tarea").performClick()

        composeRule.onNodeWithText("✓ Completada").assertExists()
        composeRule.onNodeWithText("2/2 pasos").assertExists()
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(1f, 0f..1f))).assertExists()
        composeRule.onNodeWithContentDescription("Avanzar misión Entregar tarea").assertDoesNotExist()
        composeRule.runOnIdle {
            assertEquals(1, rewards)
            assertEquals(true, missions.single().completed)
        }
    }

    @Test
    fun addDialogExposesLogicalTalkBackOrder() {
        composeRule.setContent {
            TDCoinsTheme {
                MissionsScreen(emptyList(), {}, {})
            }
        }

        composeRule.onNodeWithContentDescription("Agregar misión").performClick()
        composeRule.onNode(
            hasSetTextAction() and hasText("¿Qué quieres lograr?"),
        ).assert(SemanticsMatcher.expectValue(SemanticsProperties.TraversalIndex, 0f))
        composeRule.onNodeWithText("Categoría").assertExists()
        composeRule.onNodeWithText("Enfoque").assertExists()
    }
}