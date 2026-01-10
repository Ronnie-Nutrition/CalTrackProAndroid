package com.easyaiflows.caltrackpro.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import com.easyaiflows.caltrackpro.MainActivity
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.data.local.FastingDataStore
import com.easyaiflows.caltrackpro.domain.model.FASTING_MILESTONES
import com.easyaiflows.caltrackpro.domain.model.FastingState
import com.easyaiflows.caltrackpro.receiver.FastingAlarmReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

@AndroidEntryPoint
class FastingTimerService : Service() {

    @Inject
    lateinit var fastingDataStore: FastingDataStore

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var updateJob: Job? = null

    companion object {
        const val CHANNEL_ID_TIMER = "fasting_timer"
        const val CHANNEL_ID_ALERTS = "fasting_alerts"
        const val CHANNEL_ID_WATER = "water_reminders"

        const val NOTIFICATION_ID_TIMER = 1001
        const val NOTIFICATION_ID_MILESTONE = 1002
        const val NOTIFICATION_ID_WATER = 1003
        const val NOTIFICATION_ID_COMPLETE = 1004

        const val ACTION_START = "com.easyaiflows.caltrackpro.action.START_FASTING"
        const val ACTION_STOP = "com.easyaiflows.caltrackpro.action.STOP_FASTING"

        const val EXTRA_TARGET_HOURS = "extra_target_hours"

        private const val WATER_REMINDER_INTERVAL_HOURS = 2L
        private const val UPDATE_INTERVAL_MS = 60_000L // 1 minute

        fun startService(context: Context, targetHours: Int) {
            val intent = Intent(context, FastingTimerService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_TARGET_HOURS, targetHours)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, FastingTimerService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val targetHours = intent.getIntExtra(EXTRA_TARGET_HOURS, 16)
                startFastingTimer(targetHours)
            }
            ACTION_STOP -> {
                stopFastingTimer()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
        serviceScope.cancel()
        cancelAllAlarms()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Timer channel - ongoing, low importance (no sound)
            val timerChannel = NotificationChannel(
                CHANNEL_ID_TIMER,
                "Fasting Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows your current fasting progress"
                setShowBadge(false)
            }

            // Alerts channel - milestones and completion
            val alertsChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Fasting Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Milestone and completion notifications"
                enableVibration(true)
            }

            // Water reminders channel
            val waterChannel = NotificationChannel(
                CHANNEL_ID_WATER,
                "Water Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Hydration reminders during fasting"
            }

            notificationManager.createNotificationChannels(
                listOf(timerChannel, alertsChannel, waterChannel)
            )
        }
    }

    private fun startFastingTimer(targetHours: Int) {
        serviceScope.launch {
            val dataState = fastingDataStore.fastingDataState.first()
            val startTime = dataState.fastingStartTime ?: Instant.now()

            // Start foreground with initial notification
            val notification = buildTimerNotification(startTime, targetHours)
            startForeground(NOTIFICATION_ID_TIMER, notification)

            // Schedule milestone notifications
            scheduleMilestoneAlarms(startTime)

            // Schedule water reminders
            scheduleWaterReminders(startTime, targetHours)

            // Start periodic updates
            startPeriodicUpdates(startTime, targetHours)
        }
    }

    private fun startPeriodicUpdates(startTime: Instant, targetHours: Int) {
        updateJob?.cancel()
        updateJob = serviceScope.launch {
            while (true) {
                updateTimerNotification(startTime, targetHours)
                delay(UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun updateTimerNotification(startTime: Instant, targetHours: Int) {
        val notification = buildTimerNotification(startTime, targetHours)
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID_TIMER, notification)
    }

    private fun buildTimerNotification(startTime: Instant, targetHours: Int): Notification {
        val elapsed = Duration.between(startTime, Instant.now())
        val elapsedHours = elapsed.toHours().toInt()
        val elapsedMinutes = elapsed.toMinutes() % 60

        val remaining = Duration.ofHours(targetHours.toLong()).minus(elapsed)
        val remainingText = if (remaining.isNegative) {
            "Goal reached!"
        } else {
            val remHours = remaining.toHours()
            val remMinutes = remaining.toMinutes() % 60
            "${remHours}h ${remMinutes}m remaining"
        }

        val progress = ((elapsed.toMinutes().toFloat() / (targetHours * 60)) * 100).toInt().coerceIn(0, 100)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, FastingTimerService::class.java).apply { action = ACTION_STOP },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID_TIMER)
            .setContentTitle("Fasting: ${elapsedHours}h ${elapsedMinutes}m")
            .setContentText(remainingText)
            .setSmallIcon(R.drawable.ic_flame)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, false)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_close, "Stop", stopIntent)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()
    }

    private fun scheduleMilestoneAlarms(startTime: Instant) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        FASTING_MILESTONES.filter { it.hours > 0 }.forEach { milestone ->
            val milestoneTime = startTime.plusSeconds(milestone.hours * 3600L)

            // Only schedule if milestone is in the future
            if (milestoneTime.isAfter(Instant.now())) {
                val intent = Intent(this, FastingAlarmReceiver::class.java).apply {
                    action = FastingAlarmReceiver.ACTION_MILESTONE
                    putExtra(FastingAlarmReceiver.EXTRA_MILESTONE_HOURS, milestone.hours)
                    putExtra(FastingAlarmReceiver.EXTRA_MILESTONE_TITLE, milestone.title)
                    putExtra(FastingAlarmReceiver.EXTRA_MILESTONE_DESCRIPTION, milestone.description)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    milestone.hours, // Use hours as unique request code
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    milestoneTime.toEpochMilli(),
                    pendingIntent
                )
            }
        }
    }

    private fun scheduleWaterReminders(startTime: Instant, targetHours: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule water reminders every 2 hours during the fast
        for (hour in WATER_REMINDER_INTERVAL_HOURS..targetHours step WATER_REMINDER_INTERVAL_HOURS.toInt()) {
            val reminderTime = startTime.plusSeconds(hour * 3600L)

            if (reminderTime.isAfter(Instant.now())) {
                val intent = Intent(this, FastingAlarmReceiver::class.java).apply {
                    action = FastingAlarmReceiver.ACTION_WATER_REMINDER
                    putExtra(FastingAlarmReceiver.EXTRA_HOURS_ELAPSED, hour.toInt())
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    1000 + hour.toInt(), // Offset to avoid collision with milestone request codes
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime.toEpochMilli(),
                    pendingIntent
                )
            }
        }
    }

    private fun cancelAllAlarms() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Cancel milestone alarms
        FASTING_MILESTONES.forEach { milestone ->
            val intent = Intent(this, FastingAlarmReceiver::class.java).apply {
                action = FastingAlarmReceiver.ACTION_MILESTONE
            }
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                milestone.hours,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }

        // Cancel water reminder alarms (up to 24 hours)
        for (hour in WATER_REMINDER_INTERVAL_HOURS..24 step WATER_REMINDER_INTERVAL_HOURS.toInt()) {
            val intent = Intent(this, FastingAlarmReceiver::class.java).apply {
                action = FastingAlarmReceiver.ACTION_WATER_REMINDER
            }
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                1000 + hour.toInt(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }

    private fun stopFastingTimer() {
        updateJob?.cancel()
        cancelAllAlarms()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}
