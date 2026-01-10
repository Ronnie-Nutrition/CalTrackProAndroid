package com.easyaiflows.caltrackpro.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.RemoteViews
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.easyaiflows.caltrackpro.MainActivity
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule
import com.easyaiflows.caltrackpro.domain.model.FastingState
import com.easyaiflows.caltrackpro.receiver.FastingAlarmReceiver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.time.Duration
import java.time.Instant

private val Context.widgetDataStore by preferencesDataStore(name = "fasting_preferences")

class FastingWidgetProvider : AppWidgetProvider() {

    companion object {
        private val KEY_CURRENT_STATE = stringPreferencesKey("fasting_current_state")
        private val KEY_FASTING_START_TIME = longPreferencesKey("fasting_start_time")
        private val KEY_EATING_WINDOW_START_TIME = longPreferencesKey("eating_window_start_time")
        private val KEY_SELECTED_SCHEDULE = stringPreferencesKey("fasting_selected_schedule")
        private val KEY_CUSTOM_FASTING_HOURS = androidx.datastore.preferences.core.intPreferencesKey("fasting_custom_hours")

        const val ACTION_UPDATE_WIDGET = "com.easyaiflows.caltrackpro.UPDATE_FASTING_WIDGET"

        /**
         * Request an update of all fasting widgets.
         */
        fun requestUpdate(context: Context) {
            val intent = Intent(context, FastingWidgetProvider::class.java).apply {
                action = ACTION_UPDATE_WIDGET
            }
            context.sendBroadcast(intent)
        }

        /**
         * Update all widgets directly.
         */
        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, FastingWidgetProvider::class.java)
            val widgetIds = appWidgetManager.getAppWidgetIds(componentName)

            if (widgetIds.isNotEmpty()) {
                val intent = Intent(context, FastingWidgetProvider::class.java).apply {
                    action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds)
                }
                context.sendBroadcast(intent)
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action == ACTION_UPDATE_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, FastingWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_fasting_timer)

        // Read current state from DataStore
        val widgetState = runBlocking { readFastingState(context) }

        // Configure views based on state
        when (widgetState.state) {
            FastingState.NOT_STARTED -> configureNotStartedState(views)
            FastingState.FASTING -> configureFastingState(views, widgetState)
            FastingState.EATING -> configureEatingState(views, widgetState)
        }

        // Set click action to open fasting screen
        val clickIntent = Intent(Intent.ACTION_VIEW, Uri.parse(FastingAlarmReceiver.DEEP_LINK_FASTING)).apply {
            setClass(context, MainActivity::class.java)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun configureNotStartedState(views: RemoteViews) {
        views.setImageViewResource(R.id.widget_icon, R.drawable.ic_flame)
        views.setTextViewText(R.id.widget_title, "Fasting")
        views.setTextViewText(R.id.widget_time, "Ready")
        views.setTextViewText(R.id.widget_status, "Tap to start")
        views.setViewVisibility(R.id.widget_progress, View.GONE)
    }

    private fun configureFastingState(views: RemoteViews, widgetState: WidgetState) {
        val elapsed = Duration.between(widgetState.startTime, Instant.now())
        val elapsedHours = elapsed.toHours().toInt()
        val elapsedMinutes = (elapsed.toMinutes() % 60).toInt()
        val elapsedSeconds = (elapsed.seconds % 60).toInt()

        val targetHours = widgetState.targetHours
        val remaining = Duration.ofHours(targetHours.toLong()).minus(elapsed)

        val progress = ((elapsed.toMinutes().toFloat() / (targetHours * 60)) * 100).toInt().coerceIn(0, 100)

        views.setImageViewResource(R.id.widget_icon, R.drawable.ic_flame)
        views.setTextViewText(R.id.widget_title, "Fasting")
        views.setTextViewText(R.id.widget_time, String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds))

        if (remaining.isNegative) {
            views.setTextViewText(R.id.widget_status, "Goal reached!")
        } else {
            val remHours = remaining.toHours()
            val remMinutes = remaining.toMinutes() % 60
            views.setTextViewText(R.id.widget_status, "${remHours}h ${remMinutes}m remaining")
        }

        views.setViewVisibility(R.id.widget_progress, View.VISIBLE)
        views.setProgressBar(R.id.widget_progress, 100, progress, false)
    }

    private fun configureEatingState(views: RemoteViews, widgetState: WidgetState) {
        val elapsed = Duration.between(widgetState.eatingStartTime ?: Instant.now(), Instant.now())
        val eatingWindowHours = 24 - widgetState.targetHours
        val remaining = Duration.ofHours(eatingWindowHours.toLong()).minus(elapsed)

        views.setImageViewResource(R.id.widget_icon, R.drawable.ic_utensils)
        views.setTextViewText(R.id.widget_title, "Eating Window")

        if (remaining.isNegative) {
            views.setTextViewText(R.id.widget_time, "Done")
            views.setTextViewText(R.id.widget_status, "Ready to fast again")
        } else {
            val remHours = remaining.toHours().toInt()
            val remMinutes = (remaining.toMinutes() % 60).toInt()
            views.setTextViewText(R.id.widget_time, String.format("%02d:%02d", remHours, remMinutes))
            views.setTextViewText(R.id.widget_status, "Eating window open")
        }

        views.setViewVisibility(R.id.widget_progress, View.GONE)
    }

    private suspend fun readFastingState(context: Context): WidgetState {
        return try {
            val prefs = context.widgetDataStore.data.first()

            val stateStr = prefs[KEY_CURRENT_STATE] ?: FastingState.NOT_STARTED.name
            val state = FastingState.valueOf(stateStr)

            val fastingStartTime = prefs[KEY_FASTING_START_TIME]?.let { Instant.ofEpochMilli(it) }
            val eatingStartTime = prefs[KEY_EATING_WINDOW_START_TIME]?.let { Instant.ofEpochMilli(it) }

            val scheduleStr = prefs[KEY_SELECTED_SCHEDULE] ?: FastingSchedule.SCHEDULE_16_8.name
            val schedule = FastingSchedule.valueOf(scheduleStr)
            val customHours = prefs[KEY_CUSTOM_FASTING_HOURS] ?: 16

            val targetHours = if (schedule == FastingSchedule.CUSTOM) customHours else schedule.fastingHours

            WidgetState(
                state = state,
                startTime = fastingStartTime,
                eatingStartTime = eatingStartTime,
                targetHours = targetHours
            )
        } catch (e: Exception) {
            WidgetState(
                state = FastingState.NOT_STARTED,
                startTime = null,
                eatingStartTime = null,
                targetHours = 16
            )
        }
    }

    private data class WidgetState(
        val state: FastingState,
        val startTime: Instant?,
        val eatingStartTime: Instant?,
        val targetHours: Int
    )
}
