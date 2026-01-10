package com.easyaiflows.caltrackpro.receiver

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.easyaiflows.caltrackpro.MainActivity
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.service.FastingTimerService

class FastingAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_MILESTONE = "com.easyaiflows.caltrackpro.action.MILESTONE"
        const val ACTION_WATER_REMINDER = "com.easyaiflows.caltrackpro.action.WATER_REMINDER"
        const val ACTION_FASTING_COMPLETE = "com.easyaiflows.caltrackpro.action.FASTING_COMPLETE"
        const val ACTION_EATING_WINDOW_WARNING = "com.easyaiflows.caltrackpro.action.EATING_WINDOW_WARNING"
        const val ACTION_EATING_WINDOW_CLOSED = "com.easyaiflows.caltrackpro.action.EATING_WINDOW_CLOSED"

        const val EXTRA_MILESTONE_HOURS = "extra_milestone_hours"
        const val EXTRA_MILESTONE_TITLE = "extra_milestone_title"
        const val EXTRA_MILESTONE_DESCRIPTION = "extra_milestone_description"
        const val EXTRA_HOURS_ELAPSED = "extra_hours_elapsed"

        const val DEEP_LINK_FASTING = "caltrackpro://fasting"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_MILESTONE -> handleMilestoneAlarm(context, intent)
            ACTION_WATER_REMINDER -> handleWaterReminder(context, intent)
            ACTION_FASTING_COMPLETE -> handleFastingComplete(context)
            ACTION_EATING_WINDOW_WARNING -> handleEatingWindowWarning(context)
            ACTION_EATING_WINDOW_CLOSED -> handleEatingWindowClosed(context)
        }
    }

    private fun handleMilestoneAlarm(context: Context, intent: Intent) {
        val hours = intent.getIntExtra(EXTRA_MILESTONE_HOURS, 0)
        val title = intent.getStringExtra(EXTRA_MILESTONE_TITLE) ?: "Milestone Reached!"
        val description = intent.getStringExtra(EXTRA_MILESTONE_DESCRIPTION) ?: ""

        val contentIntent = createMainActivityIntent(context)

        val notification = NotificationCompat.Builder(context, FastingTimerService.CHANNEL_ID_ALERTS)
            .setContentTitle("${hours}h Milestone: $title")
            .setContentText(description)
            .setSmallIcon(R.drawable.ic_sparkles)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(FastingTimerService.NOTIFICATION_ID_MILESTONE + hours, notification)
    }

    private fun handleWaterReminder(context: Context, intent: Intent) {
        val hoursElapsed = intent.getIntExtra(EXTRA_HOURS_ELAPSED, 0)

        val contentIntent = createMainActivityIntent(context)

        val notification = NotificationCompat.Builder(context, FastingTimerService.CHANNEL_ID_WATER)
            .setContentTitle("Stay Hydrated!")
            .setContentText("You've been fasting for ${hoursElapsed}h. Remember to drink water.")
            .setSmallIcon(R.drawable.ic_droplet)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(FastingTimerService.NOTIFICATION_ID_WATER + hoursElapsed, notification)
    }

    private fun handleFastingComplete(context: Context) {
        val contentIntent = createMainActivityIntent(context)

        val notification = NotificationCompat.Builder(context, FastingTimerService.CHANNEL_ID_ALERTS)
            .setContentTitle("Fasting Goal Reached!")
            .setContentText("Congratulations! You've completed your fast. Time to eat!")
            .setSmallIcon(R.drawable.ic_check_circle)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(FastingTimerService.NOTIFICATION_ID_COMPLETE, notification)
    }

    private fun handleEatingWindowWarning(context: Context) {
        val contentIntent = createMainActivityIntent(context)

        val notification = NotificationCompat.Builder(context, FastingTimerService.CHANNEL_ID_ALERTS)
            .setContentTitle("Eating Window Closing Soon")
            .setContentText("You have 1 hour left in your eating window.")
            .setSmallIcon(R.drawable.ic_clock)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(FastingTimerService.NOTIFICATION_ID_COMPLETE + 1, notification)
    }

    private fun handleEatingWindowClosed(context: Context) {
        val contentIntent = createMainActivityIntent(context)

        val notification = NotificationCompat.Builder(context, FastingTimerService.CHANNEL_ID_ALERTS)
            .setContentTitle("Eating Window Closed")
            .setContentText("Your eating window has ended. Ready to start your next fast?")
            .setSmallIcon(R.drawable.ic_flame)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.notify(FastingTimerService.NOTIFICATION_ID_COMPLETE + 2, notification)
    }

    private fun createMainActivityIntent(context: Context): PendingIntent {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DEEP_LINK_FASTING)).apply {
            setClass(context, MainActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
