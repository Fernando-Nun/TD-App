package com.tdcoins.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.util.regex.Pattern

class MicrophonePermissionTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<TestActivity>()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val device = UiDevice.getInstance(instrumentation)
    private val packageName = instrumentation.targetContext.packageName

    @Test
    fun grantedPermissionAllowsVoiceFlowWithoutPermissionDialog() {
        instrumentation.uiAutomation.executeShellCommand(
            "pm grant $packageName ${Manifest.permission.RECORD_AUDIO}",
        ).close()
        assertEquals(
            PackageManager.PERMISSION_GRANTED,
            ContextCompat.checkSelfPermission(composeRule.activity, Manifest.permission.RECORD_AUDIO),
        )

        composeRule.setContent {
            TDCoinsTheme { VoiceScreen(emptyList(), {}, {}) }
        }
        composeRule.onNodeWithContentDescription("Iniciar grabación").performClick()
        composeRule.onNodeWithText("Nota transcrita o escrita").assertIsDisplayed()
    }

    @Test
    fun deniedPermissionKeepsManualEntryAvailable() {
        instrumentation.uiAutomation.executeShellCommand(
            "pm revoke $packageName ${Manifest.permission.RECORD_AUDIO}",
        ).close()
        instrumentation.uiAutomation.executeShellCommand(
            "pm clear-permission-flags $packageName ${Manifest.permission.RECORD_AUDIO} user-set user-fixed",
        ).close()

        composeRule.setContent {
            TDCoinsTheme { VoiceScreen(emptyList(), {}, {}) }
        }
        composeRule.onNodeWithContentDescription("Iniciar grabación").performClick()

        val deny = device.wait(
            Until.findObject(By.res("com.android.permissioncontroller:id/permission_deny_button")),
            5_000,
        ) ?: device.wait(
            Until.findObject(By.text(Pattern.compile("no permitir|don't allow|deny", Pattern.CASE_INSENSITIVE))),
            3_000,
        )
        checkNotNull(deny) { "No apareció el diálogo de permiso del micrófono" }
        deny.click()

        composeRule.waitUntil(5_000) {
            composeRule.onAllNodes(
                androidx.compose.ui.test.hasText(
                    "Sin permiso de micrófono puedes escribir tu nota manualmente.",
                ),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("Nota transcrita o escrita").performTextInput("Nota manual")
        composeRule.onNodeWithText("Guardar nota").assertIsDisplayed()
    }
}