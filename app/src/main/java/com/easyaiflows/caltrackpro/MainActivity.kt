package com.easyaiflows.caltrackpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.easyaiflows.caltrackpro.ui.navigation.CalTrackNavHost
import com.easyaiflows.caltrackpro.ui.theme.CalTrackProTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalTrackProTheme {
                CalTrackNavHost()
            }
        }
    }
}
