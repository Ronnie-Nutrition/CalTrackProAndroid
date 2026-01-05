package com.easyaiflows.caltrackpro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.easyaiflows.caltrackpro.data.repository.UserProfileRepository
import com.easyaiflows.caltrackpro.ui.navigation.CalTrackNavHost
import com.easyaiflows.caltrackpro.ui.theme.CalTrackProTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userProfileRepository: UserProfileRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalTrackProTheme {
                val isOnboardingCompleted by userProfileRepository.isOnboardingCompleted.collectAsState(initial = null)

                when (isOnboardingCompleted) {
                    null -> {
                        // Show loading while we check onboarding status
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        CalTrackNavHost(
                            isOnboardingCompleted = isOnboardingCompleted ?: false
                        )
                    }
                }
            }
        }
    }
}
