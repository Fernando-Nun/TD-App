package com.tdcoins.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

class AccessibilityAndLayoutTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<TestActivity>()

    @Test
    fun voiceControlsExposeLabelsAndSelectionState() {
        composeRule.setContent {
            TDCoinsTheme {
                VoiceScreen(emptyList(), {}, {})
            }
        }

        composeRule.onNodeWithContentDescription("Iniciar grabación").assertIsDisplayed()
        composeRule.onNodeWithTag("challenge-focus").performClick().assertIsSelected()
        composeRule.onNodeWithContentDescription("Se me dificulta poner atención").assertIsSelected()
    }

    @Test
    fun missionsRemainReachableAtTwoHundredPercentFontScale() {
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(
                LocalDensity provides Density(density.density, fontScale = 2f),
            ) {
                TDCoinsTheme {
                    MissionsScreen(
                        missions = listOf(
                            Mission("large-text", "Leer instrucciones importantes", MissionCategory.FOCUS, 3, 1, 20),
                        ),
                        onMissionsChange = {},
                        onReward = {},
                    )
                }
            }
        }

        composeRule.onNodeWithText("Mis Misiones").assertIsDisplayed()
        composeRule.onNodeWithText("Leer instrucciones importantes").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Avanzar misión Leer instrucciones importantes").assertIsDisplayed()
    }

    @Test
    fun phoneNavigationExposesDestinations() {
        composeRule.setContent {
            TDCoinsTheme {
                Box(Modifier.width(360.dp)) {
                    BottomNavigation(AppTab.HOME) {}
                }
            }
        }
        composeRule.onNodeWithTag("bottom-navigation").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Misiones").assertIsDisplayed()
    }

    @Test
    fun tabletNavigationExposesDestinations() {
        composeRule.setContent {
            TDCoinsTheme {
                Box(Modifier.width(800.dp)) {
                    SideNavigation(AppTab.HOME) {}
                }
            }
        }
        composeRule.onNodeWithTag("side-navigation").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Misiones").assertIsDisplayed()
    }
}