package com.tdcoins.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppNotifications.createChannel(this)
        enableEdgeToEdge()
        setContent {
            TDCoinsTheme {
                TDCoinsApp()
            }
        }
    }
}