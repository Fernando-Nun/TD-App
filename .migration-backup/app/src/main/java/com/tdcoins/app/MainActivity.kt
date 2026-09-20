package com.tdcoins.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {
    private val notificationDestination = mutableStateOf<AppTab?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppNotifications.createChannels(this)
        readDestination(intent)
        enableEdgeToEdge()
        setContent {
            TDCoinsTheme {
                TDCoinsApp(
                    notificationDestination = notificationDestination.value,
                    onDestinationConsumed = { notificationDestination.value = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readDestination(intent)
    }

    private fun readDestination(intent: Intent?) {
        notificationDestination.value = intent
            ?.getStringExtra(AppNotifications.EXTRA_DESTINATION)
            ?.let { value -> AppTab.entries.find { it.name == value } }
    }
}