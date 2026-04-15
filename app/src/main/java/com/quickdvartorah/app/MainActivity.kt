package com.quickdvartorah.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.quickdvartorah.app.navigation.AppNavHost
import com.quickdvartorah.app.ui.theme.DvarTorahAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DvarTorahAppTheme {
                AppNavHost()
            }
        }
    }
}
