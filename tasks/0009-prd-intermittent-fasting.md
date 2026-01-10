# PRD: Intermittent Fasting Timer

## 1. Introduction/Overview

The Intermittent Fasting feature provides a comprehensive fasting timer with multiple protocols (16:8, 18:6, 20:4, OMAD), real-time progress tracking, health benefit milestones, water intake tracking, and fasting history. Users can start, track, and complete fasting periods while understanding the physiological benefits occurring at each stage.

**Problem Solved:** Many users practice intermittent fasting for weight management and health benefits, but tracking fasting windows manually is tedious. This feature provides a dedicated timer with visual progress, motivational milestones, and hydration reminders to help users stick to their fasting routine.

**Android Enhancement:** This implementation matches iOS functionality while adding Android-specific features like home screen widgets for quick timer access, notification channels for customizable alerts, and Wear OS integration for glanceable fasting status.

## 2. Goals

1. **Simplify fasting tracking** - One-tap start/stop for fasting periods
2. **Educate users on benefits** - Show health milestones as fasting progresses
3. **Encourage hydration** - Track water intake with timely reminders
4. **Build consistency** - Streak tracking and history to maintain habits
5. **Support multiple protocols** - 16:8, 18:6, 20:4, OMAD, and custom schedules
6. **Provide flexibility** - Eating window tracking after fasting completes
7. **Match iOS parity** - Same milestones, timers, and functionality
8. **Enable quick access** - Widgets and notifications for at-a-glance status

## 3. User Stories

### Core Fasting
- **US-1:** As a user, I want to select my fasting protocol (16:8, 18:6, etc.) so that my timer matches my goals.
- **US-2:** As a user, I want to start my fast with one tap so that tracking is effortless.
- **US-3:** As a user, I want to see a real-time countdown of my remaining fasting time.
- **US-4:** As a user, I want to see my progress as a visual ring/circle filling up.
- **US-5:** As a user, I want to stop my fast early if needed while preserving the partial session.
- **US-6:** As a user, I want to be notified when my fasting goal is reached.

### Benefit Milestones
- **US-7:** As a user, I want to see health benefits as I reach each milestone (4h, 8h, 12h, etc.) so that I stay motivated.
- **US-8:** As a user, I want to understand what's happening in my body at each stage.

### Eating Window
- **US-9:** As a user, I want to track my eating window after completing a fast.
- **US-10:** As a user, I want to be notified when my eating window is about to close.
- **US-11:** As a user, I want to manually end my eating window to start the next fast.

### Water Tracking
- **US-12:** As a user, I want to track my water intake during fasting to stay hydrated.
- **US-13:** As a user, I want periodic reminders to drink water during long fasts.
- **US-14:** As a user, I want to see my daily water goal progress.

### History & Stats
- **US-15:** As a user, I want to see my current fasting streak so that I stay motivated.
- **US-16:** As a user, I want to view my fasting history to see past completions.
- **US-17:** As a user, I want to see my longest streak and total completed fasts.

### Quick Access
- **US-18:** As a user, I want a home screen widget to see my fasting status without opening the app.
- **US-19:** As a user, I want to start/stop fasting from the widget.

## 4. Functional Requirements

### 4.1 Fasting Schedules
1. The system must support the following fasting schedules:
   - 16:8 (16 hours fasting, 8 hours eating window)
   - 18:6 (18 hours fasting, 6 hours eating window)
   - 20:4 (20 hours fasting, 4 hours eating window)
   - OMAD/23:1 (23 hours fasting, 1 hour eating window)
   - Custom (user-defined 1-23 hours fasting)
2. The system must save the user's preferred schedule
3. The system must allow changing schedule between fasts (not during)
4. Custom schedule must validate hours are between 1 and 23

### 4.2 Fasting Timer
5. The system must display elapsed fasting time in HH:MM:SS format
6. The system must display remaining fasting time in HH:MM:SS format
7. The system must update timer every 1 second for real-time accuracy
8. The system must show progress as a circular ring (0% to 100%)
9. The system must show current fasting state: NOT_STARTED, FASTING, EATING
10. The system must persist timer state across app restarts
11. The system must continue timing in background when app is closed
12. The system must handle device reboot by recalculating elapsed time from start timestamp

### 4.3 Timer Controls
13. The system must provide "Start Fast" button when not fasting
14. The system must provide "Stop Fast" button during fasting
15. The system must provide "End Eating Window" button during eating phase
16. The system must confirm before stopping a fast early (with option to discard or save)
17. The system must automatically transition from FASTING to EATING when goal reached
18. The system must allow manual transition back to NOT_STARTED from EATING

### 4.4 Benefit Milestones
19. The system must display 8 benefit milestones during fasting:

| Hours | Benefit | Description | Icon |
|-------|---------|-------------|------|
| 0 | Fast Started | Body transitions from fed to fasting state | play.circle.fill |
| 4 | Insulin Drops | Blood sugar stabilizes, insulin levels decrease | arrow.down.circle.fill |
| 8 | Glucose Used | Body depletes glycogen (glucose) stores | flame.fill |
| 12 | Fat Burning | Ketosis begins, body burns fat for fuel | bolt.fill |
| 14 | Growth Hormone | HGH levels increase, supporting muscle preservation | arrow.up.circle.fill |
| 16 | Autophagy | Cellular cleanup process begins | sparkles |
| 18 | Deep Ketosis | Maximum fat-burning efficiency achieved | flame.circle.fill |
| 24 | Cell Regeneration | Enhanced autophagy, cellular repair and renewal | arrow.triangle.2.circlepath |

20. The system must highlight the current/next milestone during fasting
21. The system must show milestone progress (e.g., "2h until Fat Burning")
22. The system must animate milestone achievement with celebration effect
23. Milestones beyond the selected fasting goal should be shown but marked as "bonus"

### 4.5 Water Tracking
24. The system must track water intake as glasses (default goal: 8 glasses)
25. The system must allow increment/decrement of water glass count
26. The system must display current water intake vs. goal
27. The system must reset water count daily at midnight
28. The system must send water reminders every 2 hours during fasting:
    - At 2h, 4h, 6h, 8h, 10h, 12h... marks
29. Water reminders must be configurable (on/off)
30. The system must show water tracking prominently during fasting state

### 4.6 Notifications
31. The system must send notification when fasting goal is reached:
    - Title: "Fasting Complete!"
    - Body: "Congratulations! You've completed your [16:8] fast."
32. The system must send water reminder notifications (if enabled):
    - Title: "Stay Hydrated"
    - Body: "You've been fasting for [X] hours. Drink some water!"
33. The system must send eating window warning (1 hour before closing):
    - Title: "Eating Window Closing Soon"
    - Body: "Your eating window ends in 1 hour."
34. The system must send eating window closed notification:
    - Title: "Eating Window Closed"
    - Body: "Your [8]-hour eating window has ended. Ready for your next fast?"
35. All notifications must be tappable to open the fasting screen
36. Notifications must use appropriate Android notification channels

### 4.7 Statistics & History
37. The system must track and display:
    - Current streak (consecutive completed fasts)
    - Longest streak (all-time best)
    - Total fasts completed
    - This week's completed fasts
38. The system must save fasting session history (last 30 days minimum)
39. Each session record must include: start time, end time, target duration, actual duration, completed (bool)
40. The system must display history as a calendar view with completion indicators
41. The system must display history as a list view with session details
42. Streak counts only fully completed fasts (reached target duration)

### 4.8 Eating Window
43. When fasting goal is reached, system must automatically start eating window timer
44. Eating window duration = 24 hours - fasting hours (e.g., 16:8 → 8 hour window)
45. The system must display eating window time remaining
46. The system must show different UI state during eating (green theme, fork/knife icon)
47. User can manually end eating window early to start next fast
48. If eating window expires, system returns to NOT_STARTED state

### 4.9 Persistence
49. The system must persist fasting state to survive app termination
50. The system must persist: current state, start time, selected schedule, water intake
51. The system must persist fasting history for 90 days
52. The system must sync fasting data to widgets in real-time

### 4.10 Widget Support
53. The system must provide a fasting timer home screen widget
54. Widget must show: current state, elapsed/remaining time, progress ring
55. Widget must support tap to open fasting screen
56. Widget must update every minute during active fasting
57. Widget sizes: Small (2x2), Medium (4x2)

## 5. Non-Goals (Out of Scope)

1. **Fasting type education** - Won't explain different fasting protocols in depth (16:8 vs 5:2 vs alternate day)
2. **Health warnings** - Won't provide medical advice or warnings about fasting risks
3. **Calorie integration** - Won't automatically adjust calorie goals based on fasting
4. **Social features** - Won't allow sharing fasting status with friends
5. **Fasting challenges** - Won't provide group challenges or competitions
6. **Audio cues** - Won't play sounds for milestones (notifications only)
7. **Apple Watch sync** - This is Android; Wear OS is a future enhancement
8. **Multiple concurrent fasts** - Only one fasting session at a time

## 6. Design Considerations

### UI Flow
```
Bottom Navigation or Menu
    → Tap "Fasting" tab/icon
    → Fasting Screen
        → [NOT_STARTED] Schedule selector + "Start Fast" button
        → [FASTING] Timer ring + milestones + water tracking + "Stop" button
        → [EATING] Eating window timer + "End Window" button
    → Tap history icon → History Screen (calendar + list)
    → Tap settings icon → Fasting Settings (schedule, notifications, water goal)
```

### Visual Design

#### Not Started State
- Large circular button "Start Fast"
- Schedule selector dropdown above
- Stats cards below (streak, total fasts)
- Muted/neutral color scheme

#### Fasting State
- Large progress ring (fills clockwise as time passes)
- Center: elapsed time (large) / remaining time (smaller)
- Current milestone badge with icon and description
- Next milestone preview with countdown
- Water tracking row (glasses filled/empty)
- "Stop Fast" button (with confirmation)
- Color scheme: Blues and purples (calm, focused)

#### Eating State
- Different visual treatment (green accents)
- Fork and knife icon
- "Eating Window Open" header
- Time remaining in eating window
- "End Eating Window" button
- "Start New Fast" quick action

### Milestone Celebration
- When milestone reached: pulse animation on icon
- Brief toast/snackbar with benefit description
- Optional haptic feedback

### Progress Ring
- Gradient fill (blue → purple)
- Glow effect at current progress point
- Milestone markers around the ring (small dots at 4h, 8h, 12h, etc.)
- Smooth animation as progress updates

### History Calendar
- Month view with dots indicating fast days
- Green dot = completed fast
- Yellow dot = partial fast (stopped early)
- Tap day to see session details

## 7. Technical Considerations

### Architecture
```
ui/screens/FastingScreen.kt
ui/screens/FastingHistoryScreen.kt
ui/screens/FastingSettingsScreen.kt
ui/viewmodels/FastingViewModel.kt
ui/components/FastingProgressRing.kt
ui/components/MilestoneCard.kt
ui/components/WaterTracker.kt
ui/components/FastingStatsCard.kt
domain/model/FastingState.kt
domain/model/FastingSession.kt
domain/model/FastingSchedule.kt
domain/model/FastingMilestone.kt
domain/usecase/StartFastUseCase.kt
domain/usecase/StopFastUseCase.kt
domain/usecase/GetFastingStatsUseCase.kt
data/local/FastingDataStore.kt
data/repository/FastingRepository.kt
service/FastingTimerService.kt (foreground service)
receiver/FastingAlarmReceiver.kt
widget/FastingWidgetProvider.kt
widget/FastingWidgetReceiver.kt
```

### Data Models

```kotlin
enum class FastingState {
    NOT_STARTED,
    FASTING,
    EATING
}

enum class FastingSchedule(
    val displayName: String,
    val fastingHours: Int,
    val eatingHours: Int
) {
    SCHEDULE_16_8("16:8", 16, 8),
    SCHEDULE_18_6("18:6", 18, 6),
    SCHEDULE_20_4("20:4", 20, 4),
    SCHEDULE_OMAD("OMAD (23:1)", 23, 1),
    CUSTOM("Custom", 0, 0)  // User-defined
}

data class FastingSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Instant,
    val endTime: Instant? = null,
    val targetDuration: Duration,
    val schedule: FastingSchedule,
    val completed: Boolean = false
) {
    val actualDuration: Duration
        get() = Duration.between(startTime, endTime ?: Instant.now())

    val progress: Float
        get() = (actualDuration.toMinutes().toFloat() / targetDuration.toMinutes()).coerceIn(0f, 1f)
}

data class FastingMilestone(
    val hours: Int,
    val title: String,
    val description: String,
    val iconResId: Int
)

val FASTING_MILESTONES = listOf(
    FastingMilestone(0, "Fast Started", "Body transitions from fed to fasting state", R.drawable.ic_play),
    FastingMilestone(4, "Insulin Drops", "Blood sugar stabilizes, insulin levels decrease", R.drawable.ic_arrow_down),
    FastingMilestone(8, "Glucose Used", "Body depletes glycogen (glucose) stores", R.drawable.ic_flame),
    FastingMilestone(12, "Fat Burning", "Ketosis begins, body burns fat for fuel", R.drawable.ic_bolt),
    FastingMilestone(14, "Growth Hormone", "HGH levels increase, supporting muscle preservation", R.drawable.ic_arrow_up),
    FastingMilestone(16, "Autophagy", "Cellular cleanup process begins", R.drawable.ic_sparkles),
    FastingMilestone(18, "Deep Ketosis", "Maximum fat-burning efficiency achieved", R.drawable.ic_flame_circle),
    FastingMilestone(24, "Cell Regeneration", "Enhanced autophagy, cellular repair and renewal", R.drawable.ic_refresh)
)

data class FastingStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val totalFastsCompleted: Int,
    val fastsThisWeek: Int
)

data class FastingPreferences(
    val selectedSchedule: FastingSchedule = FastingSchedule.SCHEDULE_16_8,
    val customFastingHours: Int = 16,
    val waterGoalGlasses: Int = 8,
    val waterRemindersEnabled: Boolean = true,
    val notificationsEnabled: Boolean = true
)
```

### Timer Service

To ensure accurate timing even when the app is closed, use a foreground service:

```kotlin
class FastingTimerService : Service() {
    private val NOTIFICATION_ID = 1001
    private val CHANNEL_ID = "fasting_timer_channel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startFastingTimer()
            ACTION_STOP -> stopFastingTimer()
        }
        return START_STICKY
    }

    private fun startFastingTimer() {
        createNotificationChannel()
        val notification = buildOngoingNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Schedule milestone notifications
        scheduleMilestoneAlarms()

        // Update notification every minute
        startNotificationUpdates()
    }

    private fun buildOngoingNotification(): Notification {
        // Shows elapsed time, progress, tap to open app
    }
}
```

### Notification Channels

```kotlin
object NotificationChannels {
    const val FASTING_TIMER = "fasting_timer"        // Ongoing timer notification
    const val FASTING_ALERTS = "fasting_alerts"      // Milestone, completion alerts
    const val WATER_REMINDERS = "water_reminders"    // Hydration reminders
}
```

### Widget Implementation

```kotlin
class FastingWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_fasting_timer)

        // Get current fasting state from DataStore
        val state = FastingDataStore.getCurrentState()

        when (state) {
            FastingState.FASTING -> {
                views.setTextViewText(R.id.elapsed_time, formatElapsedTime())
                views.setTextViewText(R.id.status_text, "Fasting")
                views.setProgressBar(R.id.progress_ring, 100, calculateProgress(), false)
            }
            FastingState.EATING -> {
                views.setTextViewText(R.id.status_text, "Eating Window")
                views.setTextViewText(R.id.elapsed_time, formatRemainingEatingTime())
            }
            FastingState.NOT_STARTED -> {
                views.setTextViewText(R.id.status_text, "Ready to Fast")
                views.setTextViewText(R.id.elapsed_time, "--:--")
            }
        }

        // Set click intent to open app
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("destination", "fasting")
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
```

### Persistence (DataStore)

```kotlin
@Serializable
data class FastingDataState(
    val currentState: FastingState = FastingState.NOT_STARTED,
    val fastingStartTime: Long? = null,  // Epoch millis
    val eatingWindowStartTime: Long? = null,
    val selectedSchedule: FastingSchedule = FastingSchedule.SCHEDULE_16_8,
    val customFastingHours: Int = 16,
    val todayWaterIntake: Int = 0,
    val lastWaterResetDate: String = "",  // ISO date
    val sessions: List<FastingSession> = emptyList()
)

class FastingDataStore(private val context: Context) {
    private val dataStore = context.dataStore

    val fastingState: Flow<FastingDataState> = dataStore.data
        .map { preferences ->
            // Deserialize from preferences
        }

    suspend fun startFasting() {
        dataStore.edit { preferences ->
            preferences[CURRENT_STATE] = FastingState.FASTING.name
            preferences[FASTING_START_TIME] = System.currentTimeMillis()
        }
    }

    suspend fun stopFasting(completed: Boolean) {
        // Save session to history, update streaks
    }
}
```

### Streak Calculation

```kotlin
fun calculateCurrentStreak(sessions: List<FastingSession>): Int {
    val completedSessions = sessions
        .filter { it.completed }
        .sortedByDescending { it.startTime }

    if (completedSessions.isEmpty()) return 0

    var streak = 0
    var expectedDate = LocalDate.now()

    for (session in completedSessions) {
        val sessionDate = session.startTime.atZone(ZoneId.systemDefault()).toLocalDate()

        if (sessionDate == expectedDate || sessionDate == expectedDate.minusDays(1)) {
            streak++
            expectedDate = sessionDate.minusDays(1)
        } else {
            break
        }
    }

    return streak
}
```

## 8. Success Metrics

1. **Adoption Rate:** >40% of users try intermittent fasting feature within 30 days
2. **Completion Rate:** >60% of started fasts reach target duration
3. **Streak Maintenance:** Average streak length >5 days for active fasters
4. **Water Tracking:** >50% of fasting users log water intake
5. **Widget Usage:** >20% of fasting users add home screen widget
6. **Retention Impact:** Users who fast have >25% higher 30-day retention
7. **Notification Engagement:** >30% tap rate on fasting notifications

## 9. Open Questions

1. **Streak Reset Policy:** If a user misses one day, does streak reset to 0, or allow a "grace day"?

2. **Partial Fast Credit:** Should fasts that reach 80%+ of goal count as "completed" for streak purposes?

3. **Health Disclaimers:** Do we need a disclaimer that fasting may not be suitable for everyone? (diabetes, pregnancy, etc.)

4. **Time Zone Handling:** How should we handle users who travel across time zones mid-fast?

5. **Retroactive Logging:** Should users be able to log a fast they did yesterday but forgot to track?

6. **Integration with Diary:** Should we show a "fasting" indicator on the food diary for days with active fasts?

7. **Premium Gating:** Should intermittent fasting be free or premium? iOS appears to include it for all users.

---

## Appendix A: Milestone Descriptions (Full Text)

### 0 Hours - Fast Started
**What's Happening:** Your body begins transitioning from the fed state to the fasted state. Digestion of your last meal continues, and blood sugar levels start to normalize.

### 4 Hours - Insulin Drops
**What's Happening:** Insulin levels drop significantly as your body finishes processing your last meal. With lower insulin, your body can more easily access stored energy. Blood sugar stabilizes, reducing energy crashes and cravings.

### 8 Hours - Glucose Used
**What's Happening:** Your body has depleted most of its readily available glucose from your last meal. Glycogen stores in the liver begin to be tapped for energy. This marks the beginning of the metabolic switch.

### 12 Hours - Fat Burning Begins
**What's Happening:** With glucose and glycogen running low, your body increases fat oxidation. Ketone production begins as the liver converts fatty acids into ketones for fuel. You're now primarily burning fat for energy.

### 14 Hours - Growth Hormone Rises
**What's Happening:** Human Growth Hormone (HGH) levels increase significantly. HGH helps preserve muscle mass during fasting and promotes fat metabolism. This is your body's way of protecting lean tissue while burning fat.

### 16 Hours - Autophagy Begins
**What's Happening:** Cellular autophagy (self-cleaning) activates. Your cells begin breaking down and recycling damaged proteins and organelles. This process is associated with longevity and reduced disease risk.

### 18 Hours - Deep Ketosis
**What's Happening:** Your body is now efficiently running on ketones. Fat burning reaches maximum efficiency. Many people report mental clarity and stable energy at this stage as the brain efficiently uses ketones for fuel.

### 24 Hours - Cell Regeneration
**What's Happening:** Autophagy is in full effect, with significant cellular cleanup occurring. Some research suggests stem cell activation begins, supporting tissue regeneration. Immune function may be enhanced through removal of damaged cells.

## Appendix B: Notification Templates

### Fasting Complete
```
Title: "Fasting Complete! 🎉"
Body: "You've completed your 16-hour fast. Your eating window is now open for 8 hours."
Action: "View Progress" → Opens fasting screen
```

### Milestone Reached
```
Title: "Milestone: Fat Burning 🔥"
Body: "12 hours in! Your body is now primarily burning fat for fuel."
Action: "See Details" → Opens fasting screen
```

### Water Reminder
```
Title: "Hydration Check 💧"
Body: "You've been fasting for 4 hours. Stay hydrated - drink a glass of water!"
Action: "Log Water" → Opens fasting screen with water tracker
```

### Eating Window Warning
```
Title: "Eating Window Closing ⏰"
Body: "Your 8-hour eating window ends in 1 hour."
Action: "View Timer" → Opens fasting screen
```

### Eating Window Closed
```
Title: "Eating Window Closed"
Body: "Your eating window has ended. Ready to start your next fast?"
Action: "Start Fast" → Opens fasting screen
```

## Appendix C: iOS Feature Parity Checklist

| iOS Feature | Android Implementation | Status |
|-------------|----------------------|--------|
| 5 fasting schedules | Same options | Planned |
| 8 benefit milestones | Same milestones | Planned |
| Real-time timer (1s updates) | Same precision | Planned |
| Progress ring UI | Custom Compose canvas | Planned |
| Water tracking (8 glasses) | Same goal | Planned |
| Water reminders (2h intervals) | AlarmManager | Planned |
| Current/longest streak | Same logic | Planned |
| Fasting history (30 days) | Same retention | Planned |
| Calendar view | LazyVerticalGrid | Planned |
| Eating window tracking | Same state machine | Planned |
| Fasting complete notification | NotificationManager | Planned |
| Eating window notifications | NotificationManager | Planned |
| Background timer persistence | Foreground Service | Planned |
| Widget support | Glance/AppWidgets | Planned |

## Appendix D: Android-Specific Enhancements

1. **Fasting Widget (Glance):**
   - Small widget: Progress ring + elapsed time
   - Medium widget: Full status + quick start/stop button

2. **Notification Channels:**
   - Separate channels for timer, alerts, and water reminders
   - Users can customize importance per channel

3. **Wear OS Integration (Future):**
   - Fasting tile showing current status
   - Complication for watch faces
   - Start/stop from watch

4. **Google Fit Integration (Future):**
   - Log fasting sessions to Google Fit
   - Sync with other health apps

5. **Material You:**
   - Dynamic color for progress ring based on wallpaper
   - Milestone icons adapt to theme

6. **Quick Settings Tile (Future):**
   - Add fasting toggle to quick settings panel
   - One-tap start/stop without opening app

## Appendix E: State Machine Diagram

```
                    ┌─────────────────┐
                    │   NOT_STARTED   │
                    └────────┬────────┘
                             │
                    startFast()
                             │
                             ▼
                    ┌─────────────────┐
           ┌───────│     FASTING     │───────┐
           │       └────────┬────────┘       │
           │                │                │
    stopFast()        goalReached()    stopFast()
    (discard)              │           (save partial)
           │                │                │
           │                ▼                │
           │       ┌─────────────────┐       │
           │       │     EATING      │       │
           │       └────────┬────────┘       │
           │                │                │
           │    endEatingWindow()            │
           │    or windowExpires()           │
           │                │                │
           └───────►┌───────┴────────┐◄──────┘
                    │   NOT_STARTED   │
                    └─────────────────┘
```
