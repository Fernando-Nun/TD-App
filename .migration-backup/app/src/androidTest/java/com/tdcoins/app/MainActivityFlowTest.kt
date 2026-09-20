package com.tdcoins.app

import android.Manifest
import android.content.Context
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityFlowTest {
    @get:Rule
    val composeRule = createEmptyComposeRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private var scenario: ActivityScenario<MainActivity>? = null

    @Before
    fun prepareSignedInState() {
        context.getSharedPreferences("td_coins_state", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        AppPersistence(context).apply {
            saveSession("instrumented-test-token", "instrumented-user")
            save(
                AppSnapshot(
                    missions = listOf(
                        Mission("baseline", "Misión inicial", MissionCategory.FOCUS, 2, 0, 10),
                    ),
                ),
            )
        }
        InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
            "pm grant ${context.packageName} ${Manifest.permission.POST_NOTIFICATIONS}",
        ).close()
    }

    @After
    fun cleanUp() {
        scenario?.close()
        context.getSharedPreferences("td_coins_state", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun createsCompletesAndRestoresMissionThroughMainActivity() {
        launchApp()
        composeRule.onNodeWithContentDescription("Misiones").performClick()
        composeRule.onNodeWithContentDescription("Agregar misión").performClick()
        composeRule.onNode(
            hasSetTextAction() and hasText("¿Qué quieres lograr?"),
        ).performTextReplacement("Flujo completo")
        composeRule.onNode(
            hasSetTextAction() and hasText("Pasos meta"),
        ).performTextReplacement("1")
        composeRule.onNodeWithText("Agregar Misión").performClick()
        composeRule.onNodeWithContentDescription("Avanzar misión Flujo completo").performClick()
        composeRule.onNodeWithText("✓ Completada").assertExists()
        composeRule.waitForIdle()

        scenario?.close()
        launchApp()
        composeRule.onNodeWithContentDescription("Misiones").performClick()
        composeRule.onNodeWithText("Flujo completo").assertExists()
        composeRule.onNodeWithText("1/1 pasos").assertExists()
        composeRule.onNodeWithText("✓ Completada").assertExists()
    }

    private fun launchApp() {
        scenario = ActivityScenario.launch(MainActivity::class.java)
        composeRule.waitForIdle()
    }
}