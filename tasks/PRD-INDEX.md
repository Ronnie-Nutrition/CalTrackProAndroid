# CalTrackPro Android - PRD Index

This index provides an overview of all Product Requirement Documents (PRDs) for the CalTrackPro Android project.

## Project Goal
Create an Android version of CalTrackPro with feature parity to the iOS app.

---

## Phase 1: Core Features

### To Be Implemented
- [x] `0001-prd-food-diary.md` - Food Diary Core Functionality ✅ Complete
- [x] `0002-prd-food-search.md` - Edamam API Food Search ✅ Complete
- [ ] `0003-prd-barcode-scanning.md` - Barcode Scanning with ML Kit
- [ ] `0004-prd-recipe-management.md` - Recipe Management System
- [ ] `0005-prd-nutrition-insights.md` - Nutrition Insights & Charts
- [ ] `0006-prd-user-profile.md` - User Profile & Goals

## Phase 2: Enhanced Features

- [ ] `0007-prd-voice-input.md` - Voice-to-Food Logging
- [ ] `0008-prd-google-fit-integration.md` - Google Fit/Health Connect Sync
- [ ] `0009-prd-intermittent-fasting.md` - Fasting Timer & Tracking
- [ ] `0010-prd-widgets.md` - Home Screen Widgets

## Phase 3: Premium & Monetization

- [ ] `0011-prd-premium-subscription.md` - Google Play Billing Integration
- [ ] `0012-prd-premium-features.md` - Premium Feature Gates

## Phase 4: Infrastructure

- [ ] `0013-prd-offline-mode.md` - Offline Support & Caching
- [ ] `0014-prd-security.md` - Security Hardening
- [ ] `0015-prd-crash-reporting.md` - Firebase Crashlytics

---

## iOS Feature Reference

All features should match the iOS CalTrackPro app functionality:

### Core (iOS Completed)
- Food Diary - Track daily nutritional intake
- Food Search - Search foods via Edamam API
- Barcode Scanning - Professional scanner with torch control
- Recipe Management - Create and save custom recipes
- Nutrition Insights - Charts and trend visualization
- User Profile - Goals and dietary preferences

### Enhanced (iOS Completed)
- Voice Input - "I ate a turkey sandwich" speech recognition
- Apple Health Integration (→ Google Fit for Android)
- Intermittent Fasting - Timer, benefits timeline, water tracking
- Home Screen Widgets - Calorie progress, fasting timer

### Premium (iOS Completed)
- Monthly: $4.99/month
- Yearly: $39.99/year
- Lifetime: $79.99 one-time
- Features: AI recognition, advanced analytics, meal planning

---

## Workflow

For each feature:
1. Create PRD using `ai-dev-tasks/create-prd.md` template
2. Generate task list using `ai-dev-tasks/generate-tasks.md`
3. Implement using `ai-dev-tasks/process-task-list.md`
4. Test on emulator and physical device
5. Commit with conventional commits

---

## Status Legend
- [ ] Not Started
- [~] In Progress
- [x] Completed
