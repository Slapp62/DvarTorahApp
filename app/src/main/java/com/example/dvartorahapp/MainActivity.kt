package com.example.dvartorahapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.dvartorahapp.navigation.AppNavHost
import com.example.dvartorahapp.ui.theme.DvarTorahAppTheme
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
