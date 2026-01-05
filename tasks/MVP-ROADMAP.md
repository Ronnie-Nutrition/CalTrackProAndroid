# CalTrackPro Android - MVP Roadmap

## Current Status: 2 of 6 Phase 1 Features Complete

| Feature | Status | Priority |
|---------|--------|----------|
| Food Diary | ✅ Complete | - |
| Food Search (Edamam API) | ✅ Complete | - |
| Barcode Scanning | ❌ Not Started | **HIGH** |
| Recipe Management | ❌ Not Started | Medium |
| Nutrition Insights | ❌ Not Started | Medium |
| User Profile & Goals | ❌ Not Started | **HIGH** |

---

## MVP Definition

A **Minimum Viable Product** for CalTrackPro Android should include:

### Must-Have (MVP Core)
1. ✅ **Food Diary** - Track daily food intake
2. ✅ **Food Search** - Search Edamam API for foods
3. **Barcode Scanning** - Quick food entry via barcode
4. **User Profile** - Set calorie/macro goals

### Nice-to-Have (Post-MVP)
5. Recipe Management - Custom recipes
6. Nutrition Insights - Charts and trends
7. Voice Input
8. Google Fit Integration
9. Intermittent Fasting
10. Premium Features

---

## Prioritized Implementation Order

### Phase 1A: MVP Core (Next 2 Features)

#### 1. User Profile & Goals (Priority: HIGH)
**Why first:** Without goals, the daily summary can't show progress toward targets. This is essential for the app to be useful.

**Scope:**
- User profile screen (age, weight, height, activity level)
- Calorie goal calculation
- Macro targets (protein, carbs, fat percentages)
- Persist goals in DataStore/Room
- Update DailySummaryCard to show progress vs goals

**Estimated Tasks:** ~15-20 sub-tasks

#### 2. Barcode Scanning (Priority: HIGH)
**Why second:** Barcode scanning is the #1 convenience feature. Users expect to scan products for quick entry.

**Scope:**
- ML Kit barcode scanning integration
- Camera permission handling
- Scan result lookup via Edamam barcode API
- Add scanned food to diary flow
- Torch/flashlight toggle
- Haptic feedback on successful scan

**Estimated Tasks:** ~20-25 sub-tasks

---

### Phase 1B: Enhanced Core (After MVP)

#### 3. Nutrition Insights (Priority: Medium)
- Weekly/monthly macro trends
- Calorie history chart
- Goal achievement tracking

#### 4. Recipe Management (Priority: Medium)
- Create custom recipes
- Calculate per-serving nutrition
- Add recipe servings to diary

---

## Recommended Implementation Timeline

```
MVP Core (Current Focus):
├── User Profile & Goals  ← START HERE
│   └── Enables progress tracking
├── Barcode Scanning
│   └── Key convenience feature
│
Post-MVP Enhancements:
├── Nutrition Insights
├── Recipe Management
├── Voice Input
├── Google Fit Integration
└── Premium Features
```

---

## Next Steps

1. **Create PRD for User Profile** (`0006-prd-user-profile.md`)
2. Generate task list for User Profile
3. Implement User Profile feature
4. **Create PRD for Barcode Scanning** (`0003-prd-barcode-scanning.md`)
5. Generate task list for Barcode Scanning
6. Implement Barcode Scanning feature

---

## Technical Debt to Address Before Launch

- [ ] Add unit tests for repositories
- [ ] Add UI tests for critical flows
- [ ] Implement ProGuard/R8 rules
- [ ] Add Firebase Crashlytics
- [ ] Security audit (API key storage)
- [ ] Performance profiling

---

## Google Play Store Readiness

- [ ] App icon (512x512)
- [ ] Feature graphic (1024x500)
- [ ] Screenshots (phone + tablet)
- [ ] Privacy policy URL
- [ ] App description
- [ ] Content rating
- [ ] Data safety form
