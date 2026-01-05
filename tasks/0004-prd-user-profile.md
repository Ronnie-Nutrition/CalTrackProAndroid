# PRD: User Profile & Goals

## 1. Introduction/Overview

CalTrackPro currently uses hardcoded default nutrition goals (2000 calories, 150g protein, 250g carbs, 65g fat). This feature will allow users to create a personalized profile and set customized nutrition goals based on their body metrics, activity level, and weight goals.

**Problem:** Without personalized goals, users can't track meaningful progress toward their individual nutrition targets. A 5'2" sedentary woman has very different calorie needs than a 6'2" active man.

**Solution:** Implement a user profile system that calculates personalized calorie and macro targets using the Mifflin-St Jeor formula, with the ability to manually override goals.

---

## 2. Goals

1. Allow users to input their personal metrics (age, sex, weight, height)
2. Calculate personalized calorie goals using BMR/TDEE formulas
3. Set macro targets based on percentage distributions or diet presets
4. Support weight goals (lose, maintain, gain)
5. Persist user profile and goals across app sessions
6. Update the DailySummaryCard to reflect personalized goals
7. Provide a first-time onboarding experience for new users
8. Support both metric (kg/cm) and imperial (lbs/ft-in) units

---

## 3. User Stories

1. **As a new user**, I want to be guided through setting up my profile on first launch, so that the app immediately shows personalized goals.

2. **As a user**, I want to enter my age, sex, weight, and height, so that the app can calculate my daily calorie needs.

3. **As a user**, I want to select my activity level (Sedentary, Active, Very Active), so that my calorie calculation accounts for my lifestyle.

4. **As a user**, I want to choose a weight goal (lose weight, maintain, gain weight), so that my calorie target is adjusted accordingly.

5. **As a user**, I want to select a macro distribution preset (Balanced, Low Carb, High Protein), so that I don't have to manually calculate percentages.

6. **As a user**, I want to customize my macro percentages manually, so that I can fine-tune my nutrition targets.

7. **As a user**, I want to override the calculated calorie goal with my own number, so that I have full control over my targets.

8. **As a user**, I want to switch between metric and imperial units, so that I can use the measurement system I'm familiar with.

9. **As a user**, I want to edit my profile from a Settings screen, so that I can update my information as it changes.

---

## 4. Functional Requirements

### 4.1 User Profile Data

The system must collect and store the following user data:

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| Age | Integer | Yes | 13-120 years |
| Sex | Enum | Yes | Male, Female |
| Weight | Double | Yes | 20-500 kg / 44-1100 lbs |
| Height | Double | Yes | 100-250 cm / 3'3"-8'2" |
| Activity Level | Enum | Yes | Sedentary, Active, Very Active |
| Weight Goal | Enum | Yes | Lose, Maintain, Gain |
| Unit System | Enum | Yes | Metric, Imperial |

### 4.2 Calorie Calculation (Mifflin-St Jeor Formula)

**BMR Calculation:**
- Male: BMR = (10 × weight in kg) + (6.25 × height in cm) - (5 × age) + 5
- Female: BMR = (10 × weight in kg) + (6.25 × height in cm) - (5 × age) - 161

**TDEE (Total Daily Energy Expenditure):**
- Sedentary: BMR × 1.2
- Active: BMR × 1.55
- Very Active: BMR × 1.9

**Goal Adjustment:**
- Lose Weight: TDEE - 500 calories (1 lb/week loss)
- Maintain: TDEE
- Gain Weight: TDEE + 300 calories (lean gain)

### 4.3 Macro Distribution Presets

| Preset | Protein | Carbs | Fat | Description |
|--------|---------|-------|-----|-------------|
| Balanced | 30% | 40% | 30% | General healthy eating |
| Low Carb | 35% | 25% | 40% | Reduced carbohydrate intake |
| High Protein | 40% | 35% | 25% | Muscle building focus |
| Custom | User-defined | User-defined | User-defined | Manual percentages |

**Gram Calculation from Percentages:**
- Protein: (calories × protein%) / 4 calories per gram
- Carbs: (calories × carbs%) / 4 calories per gram
- Fat: (calories × fat%) / 9 calories per gram

### 4.4 Onboarding Flow

The system must display a first-time setup wizard with the following screens:

1. **Welcome Screen** - Introduction to CalTrackPro
2. **Personal Info Screen** - Age, sex input
3. **Body Metrics Screen** - Weight, height input with unit toggle
4. **Activity Level Screen** - Selection with descriptions
5. **Weight Goal Screen** - Lose/Maintain/Gain selection
6. **Macro Preset Screen** - Diet style selection
7. **Review Screen** - Show calculated goals, allow manual override
8. **Completion Screen** - Confirmation and proceed to diary

### 4.5 Settings/Profile Screen

The system must provide a Settings screen accessible from the Diary screen with:

1. **Profile Section**
   - Edit personal info (age, sex)
   - Edit body metrics (weight, height)
   - Change activity level
   - Change weight goal

2. **Goals Section**
   - View/edit calorie goal (show calculated vs custom)
   - Change macro preset
   - Edit macro percentages manually
   - "Reset to calculated" button

3. **Preferences Section**
   - Unit system toggle (Metric/Imperial)

### 4.6 Data Persistence

1. Store all profile data in Preferences DataStore
2. Goals must persist across app restarts
3. Profile updates must immediately reflect in DailySummaryCard
4. Store whether onboarding has been completed (boolean flag)

### 4.7 Integration with Existing Features

1. Update `NutritionGoals` model to load from DataStore instead of defaults
2. DiaryViewModel must observe profile/goals changes reactively
3. DailySummaryCard must display personalized goals

---

## 5. Non-Goals (Out of Scope)

1. **Cloud sync** - Profile data stays on device only (no account system)
2. **Weight tracking history** - Only current weight stored, no progress charts
3. **Meal planning suggestions** - No AI-based meal recommendations
4. **Micronutrient goals** - Focus on calories and macros only (fiber, sodium, etc. remain default)
5. **Multiple profiles** - Single user per device
6. **BMI/body composition analysis** - No health assessments or recommendations

---

## 6. Design Considerations

### UI Components

- Use Material 3 components consistent with existing app design
- Onboarding should use full-screen pages with navigation dots
- Settings screen should use grouped sections with dividers
- Form inputs should have clear labels and validation messages
- Unit conversion should be seamless with live preview

### Navigation

- Add Settings icon to DiaryScreen TopAppBar
- Onboarding bypasses normal navigation (shown before Diary)
- Settings screen is a new route: `NavRoutes.Settings`

### Accessibility

- All inputs must have content descriptions
- Error messages must be announced by screen readers
- Sufficient color contrast for all text

---

## 7. Technical Considerations

### Dependencies

- **Preferences DataStore** - Already available in AndroidX (add to build.gradle)
- No new external dependencies required

### Architecture

- Create `UserProfileRepository` for DataStore operations
- Create `UserProfile` domain model
- Inject repository into `DiaryViewModel`
- Use `Flow<UserProfile>` for reactive updates

### Files to Create/Modify

**New Files:**
- `domain/model/UserProfile.kt` - User profile data class
- `domain/model/ActivityLevel.kt` - Activity level enum
- `domain/model/WeightGoal.kt` - Weight goal enum
- `domain/model/MacroPreset.kt` - Macro preset enum
- `domain/model/UnitSystem.kt` - Unit system enum
- `data/repository/UserProfileRepository.kt` - DataStore operations
- `di/DataStoreModule.kt` - Hilt module for DataStore
- `ui/profile/ProfileScreen.kt` - Settings/profile editing
- `ui/profile/ProfileViewModel.kt` - Profile screen logic
- `ui/onboarding/OnboardingScreen.kt` - Setup wizard
- `ui/onboarding/OnboardingViewModel.kt` - Onboarding logic
- `util/NutritionCalculator.kt` - BMR/TDEE calculations

**Modified Files:**
- `ui/navigation/NavRoutes.kt` - Add Settings, Onboarding routes
- `ui/navigation/CalTrackNavHost.kt` - Add new screen destinations
- `ui/diary/DiaryScreen.kt` - Add settings navigation
- `ui/diary/DiaryViewModel.kt` - Observe profile changes
- `domain/model/NutritionGoals.kt` - May need updates for percentage-based goals
- `MainActivity.kt` - Check onboarding completion on launch

---

## 8. Success Metrics

1. **Onboarding completion rate** - 90%+ of new users complete profile setup
2. **Goal customization** - 50%+ of users modify at least one goal after calculation
3. **Settings engagement** - Users update profile at least once per month on average
4. **No increase in crashes** - Profile feature maintains app stability

---

## 9. Open Questions

1. Should we show a "recalculate goals" prompt when user updates weight?
2. Should macro presets be expandable in future (e.g., Keto at 5% carbs)?
3. Should we persist weight history for future "Progress" feature?
4. Should onboarding be skippable, defaulting to generic goals?

---

## Appendix: Calculation Examples

**Example User:**
- Age: 30, Sex: Male, Weight: 80kg, Height: 180cm
- Activity: Active, Goal: Maintain

**Calculation:**
1. BMR = (10 × 80) + (6.25 × 180) - (5 × 30) + 5 = 800 + 1125 - 150 + 5 = **1780 cal**
2. TDEE = 1780 × 1.55 = **2759 cal** (rounded to 2760)
3. With "Balanced" preset (30/40/30):
   - Protein: (2760 × 0.30) / 4 = **207g**
   - Carbs: (2760 × 0.40) / 4 = **276g**
   - Fat: (2760 × 0.30) / 9 = **92g**
