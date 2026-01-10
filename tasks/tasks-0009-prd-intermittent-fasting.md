# Tasks: Intermittent Fasting Timer

## Relevant Files

- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/FastingState.kt` - Enum: NOT_STARTED, FASTING, EATING
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/FastingSchedule.kt` - Enum for fasting protocols
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/FastingSession.kt` - Session data class
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/FastingMilestone.kt` - Milestone data class
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/FastingStats.kt` - Stats data class
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/FastingDataStore.kt` - DataStore persistence
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FastingRepository.kt` - Repository interface
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FastingRepositoryImpl.kt` - Repository implementation
- `app/src/main/java/com/easyaiflows/caltrackpro/service/FastingTimerService.kt` - Foreground service for background timing
- `app/src/main/java/com/easyaiflows/caltrackpro/receiver/FastingAlarmReceiver.kt` - Alarm receiver for notifications
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/FastingScreen.kt` - Main fasting screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/FastingViewModel.kt` - State management
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/FastingUiState.kt` - UI state sealed class
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/FastingHistoryScreen.kt` - History calendar/list
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/FastingSettingsScreen.kt` - Settings screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/components/FastingProgressRing.kt` - Animated progress ring
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/components/MilestoneCard.kt` - Milestone display
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/components/WaterTracker.kt` - Water intake UI
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/fasting/components/FastingStatsCard.kt` - Stats display
- `app/src/main/java/com/easyaiflows/caltrackpro/widget/FastingWidgetProvider.kt` - Home screen widget
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Add Fasting routes
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Add navigation entries
- `app/src/main/res/xml/fasting_widget_info.xml` - Widget configuration
- `app/src/main/res/layout/widget_fasting_timer.xml` - Widget layout

### Notes

- Use Foreground Service for accurate background timing (required for Android 8+)
- Timer must survive app termination and device reboot
- Use AlarmManager for scheduled notifications (milestone, water reminders)
- DataStore for persistence; Room not needed (simple key-value data)
- Notification channels required for Android 8+ notification customization
- Run `./gradlew assembleDebug` to verify compilation after each major task

## Tasks

- [x] 1.0 Create Fasting Domain Models
  - [x] 1.1 Create `FastingState.kt` enum: NOT_STARTED, FASTING, EATING
  - [x] 1.2 Create `FastingSchedule.kt` enum with displayName, fastingHours, eatingHours: SCHEDULE_16_8, SCHEDULE_18_6, SCHEDULE_20_4, SCHEDULE_OMAD, CUSTOM
  - [x] 1.3 Create `FastingSession.kt` data class with id, startTime, endTime, targetDuration, schedule, completed
  - [x] 1.4 Add computed properties to FastingSession: actualDuration, progress (0.0-1.0)
  - [x] 1.5 Create `FastingMilestone.kt` data class with hours, title, description, iconResId
  - [x] 1.6 Create `FASTING_MILESTONES` constant list with 8 milestones (0h, 4h, 8h, 12h, 14h, 16h, 18h, 24h)
  - [x] 1.7 Create `FastingStats.kt` data class with currentStreak, longestStreak, totalFastsCompleted, fastsThisWeek
  - [x] 1.8 Create `FastingPreferences.kt` data class with selectedSchedule, customFastingHours, waterGoalGlasses, remindersEnabled

- [ ] 2.0 Implement Fasting Data Persistence
  - [ ] 2.1 Create `FastingDataStore.kt` using DataStore preferences
  - [ ] 2.2 Implement keys for: currentState, fastingStartTime, eatingWindowStartTime, selectedSchedule, customHours, waterIntake, lastWaterResetDate
  - [ ] 2.3 Implement `saveFastingState(state, startTime)` function
  - [ ] 2.4 Implement `getFastingState(): Flow<FastingDataState>` reactive getter
  - [ ] 2.5 Implement `saveSession(session)` to persist completed sessions
  - [ ] 2.6 Implement `getSessions(lastNDays: Int): List<FastingSession>` for history
  - [ ] 2.7 Implement water tracking: `incrementWater()`, `decrementWater()`, `resetWaterIfNewDay()`
  - [ ] 2.8 Create `FastingRepository.kt` interface abstracting DataStore
  - [ ] 2.9 Create `FastingRepositoryImpl.kt` implementation
  - [ ] 2.10 Add `@Binds` entry in RepositoryModule for FastingRepository

- [ ] 3.0 Implement Fasting Timer Service
  - [ ] 3.1 Create `FastingTimerService.kt` extending Service
  - [ ] 3.2 Create notification channel "fasting_timer" for ongoing timer notification
  - [ ] 3.3 Implement `startForeground()` with persistent notification showing elapsed time
  - [ ] 3.4 Implement notification update every minute showing current progress
  - [ ] 3.5 Handle `ACTION_START` and `ACTION_STOP` intents
  - [ ] 3.6 Recalculate elapsed time from saved startTime on service start (handles reboot)
  - [ ] 3.7 Schedule milestone notifications using AlarmManager
  - [ ] 3.8 Schedule water reminder alarms (every 2 hours during fasting)
  - [ ] 3.9 Add service declaration to AndroidManifest.xml with FOREGROUND_SERVICE permission
  - [ ] 3.10 Create `FastingAlarmReceiver.kt` BroadcastReceiver for scheduled alarms

- [ ] 4.0 Implement Notification System
  - [ ] 4.1 Create notification channel "fasting_alerts" for milestone/completion alerts
  - [ ] 4.2 Create notification channel "water_reminders" for hydration reminders
  - [ ] 4.3 Implement `showFastingCompleteNotification()` with congratulations message
  - [ ] 4.4 Implement `showMilestoneNotification(milestone)` with benefit description
  - [ ] 4.5 Implement `showWaterReminderNotification(hoursElapsed)` with hydration prompt
  - [ ] 4.6 Implement `showEatingWindowWarningNotification()` (1 hour before closing)
  - [ ] 4.7 Implement `showEatingWindowClosedNotification()`
  - [ ] 4.8 Add PendingIntent to all notifications to open FastingScreen on tap
  - [ ] 4.9 Handle notification permission request (Android 13+)
  - [ ] 4.10 Allow users to toggle notification types in settings

- [ ] 5.0 Create Fasting UI Components
  - [ ] 5.1 Create `FastingUiState.kt` sealed class: NotStarted, Fasting, Eating, Loading
  - [ ] 5.2 Create `FastingProgressRing.kt` using Canvas with gradient fill (blue → purple)
  - [ ] 5.3 Add milestone marker dots around progress ring at 4h, 8h, 12h, 14h, 16h, 18h, 24h positions
  - [ ] 5.4 Add glow effect at current progress point on ring
  - [ ] 5.5 Implement smooth animation for progress updates (animateFloatAsState)
  - [ ] 5.6 Create `MilestoneCard.kt` showing current milestone icon, title, description
  - [ ] 5.7 Add next milestone preview with "X hours until [milestone]" countdown
  - [ ] 5.8 Create `WaterTracker.kt` with glass icons (filled/empty), increment/decrement buttons
  - [ ] 5.9 Create `FastingStatsCard.kt` showing streak, total fasts, this week's count
  - [ ] 5.10 Create `ScheduleSelector.kt` dropdown for selecting fasting protocol
  - [ ] 5.11 Add custom hours input when CUSTOM schedule selected (1-23 validation)

- [ ] 6.0 Implement Main Fasting Screen and ViewModel
  - [ ] 6.1 Create `FastingEvent.kt` sealed class for navigation/actions
  - [ ] 6.2 Create `FastingViewModel.kt` with StateFlow for UI state
  - [ ] 6.3 Inject FastingRepository and observe fasting state as Flow
  - [ ] 6.4 Implement `startFasting()` - save state, start service, schedule notifications
  - [ ] 6.5 Implement `stopFasting(save: Boolean)` - stop service, optionally save session
  - [ ] 6.6 Implement `endEatingWindow()` - transition to NOT_STARTED
  - [ ] 6.7 Implement timer tick logic: calculate elapsed, remaining, progress every second
  - [ ] 6.8 Implement automatic transition from FASTING to EATING when goal reached
  - [ ] 6.9 Implement `getCurrentMilestone()` and `getNextMilestone()` based on elapsed time
  - [ ] 6.10 Implement `incrementWater()` and `decrementWater()` with daily reset check
  - [ ] 6.11 Create `FastingScreen.kt` composable with three state views
  - [ ] 6.12 Build NOT_STARTED state: schedule selector + "Start Fast" button + stats cards
  - [ ] 6.13 Build FASTING state: progress ring + timer + milestones + water tracker + stop button
  - [ ] 6.14 Build EATING state: eating window timer + green theme + end window button
  - [ ] 6.15 Add confirmation dialog for stopping fast early
  - [ ] 6.16 Add celebration animation when milestone reached (pulse + haptic)

- [ ] 7.0 Implement Fasting History Screen
  - [ ] 7.1 Create `FastingHistoryScreen.kt` composable
  - [ ] 7.2 Implement calendar month view using LazyVerticalGrid
  - [ ] 7.3 Add colored dots on calendar: green = completed, yellow = partial
  - [ ] 7.4 Implement day selection to show session details
  - [ ] 7.5 Create `FastingSessionCard.kt` showing date, duration, schedule, completed status
  - [ ] 7.6 Implement list view toggle (calendar vs. list)
  - [ ] 7.7 Calculate and display streak information at top
  - [ ] 7.8 Implement `calculateCurrentStreak()` logic counting consecutive completed fasts
  - [ ] 7.9 Implement `calculateLongestStreak()` from all history
  - [ ] 7.10 Add navigation to history from main fasting screen (history icon in top bar)

- [ ] 8.0 Implement Fasting Settings Screen
  - [ ] 8.1 Create `FastingSettingsScreen.kt` composable
  - [ ] 8.2 Add default schedule selector (saves preference)
  - [ ] 8.3 Add custom fasting hours input (1-23)
  - [ ] 8.4 Add water goal setting (glasses per day, default 8)
  - [ ] 8.5 Add notification toggles: fasting complete, milestones, water reminders, eating window
  - [ ] 8.6 Save all preferences to DataStore
  - [ ] 8.7 Add navigation to settings from main fasting screen (settings icon in top bar)

- [ ] 9.0 Integrate Navigation
  - [ ] 9.1 Add `Fasting` route to NavRoutes.kt
  - [ ] 9.2 Add `FastingHistory` route to NavRoutes.kt
  - [ ] 9.3 Add `FastingSettings` route to NavRoutes.kt
  - [ ] 9.4 Add composable entries in CalTrackNavHost.kt for all three screens
  - [ ] 9.5 Add "Fasting" tab/icon to bottom navigation or top bar
  - [ ] 9.6 Handle deep link from notification tap to FastingScreen

- [ ] 10.0 Implement Home Screen Widget
  - [ ] 10.1 Create `widget_fasting_timer.xml` layout with RemoteViews
  - [ ] 10.2 Create `fasting_widget_info.xml` with widget metadata (sizes, preview, etc.)
  - [ ] 10.3 Create `FastingWidgetProvider.kt` extending AppWidgetProvider
  - [ ] 10.4 Implement `onUpdate()` to refresh widget with current fasting state
  - [ ] 10.5 Read fasting state from DataStore in widget provider
  - [ ] 10.6 Display different layouts for NOT_STARTED, FASTING, EATING states
  - [ ] 10.7 Show elapsed/remaining time and progress in FASTING state
  - [ ] 10.8 Add tap action to open FastingScreen via PendingIntent
  - [ ] 10.9 Schedule widget updates every minute during active fasting
  - [ ] 10.10 Register widget in AndroidManifest.xml with receiver and metadata
  - [ ] 10.11 Trigger widget update from ViewModel when state changes

- [ ] 11.0 Add Error Handling and Edge Cases
  - [ ] 11.1 Handle device reboot: recalculate state from saved startTime
  - [ ] 11.2 Handle timezone changes during active fast
  - [ ] 11.3 Handle water reset at midnight (check lastResetDate)
  - [ ] 11.4 Validate custom hours input (1-23 range)
  - [ ] 11.5 Handle notification permission denied gracefully
  - [ ] 11.6 Handle foreground service restrictions (battery optimization)
  - [ ] 11.7 Add "Don't optimize" battery prompt for reliable background timing
  - [ ] 11.8 Track analytics: fast_started, fast_completed, fast_stopped_early, milestone_reached, water_logged
