package com.easyaiflows.caltrackpro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.easyaiflows.caltrackpro.data.local.FastingDataStore
import com.easyaiflows.caltrackpro.domain.model.FastingState
import com.easyaiflows.caltrackpro.service.FastingTimerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootCompletedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var fastingDataStore: FastingDataStore

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Use goAsync() for longer work in broadcast receiver
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val dataState = fastingDataStore.fastingDataState.first()

                    // If we were fasting before reboot, restart the service
                    if (dataState.currentState == FastingState.FASTING && dataState.fastingStartTime != null) {
                        val targetHours = if (dataState.selectedSchedule == com.easyaiflows.caltrackpro.domain.model.FastingSchedule.CUSTOM) {
                            dataState.customFastingHours
                        } else {
                            dataState.selectedSchedule.fastingHours
                        }

                        FastingTimerService.startService(context, targetHours)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
