# Session Handoff Summary

## Product State

- App name is now `Quick Dvar Torah`.
- Android package name is now `com.quickdvartorah.app`.
- Firebase app config was updated for the new package name.
- Google Play app entry exists and Play App Signing SHA values were added to Firebase.
- Google Form desktop submission flow is working end to end.
- Ads are disabled for launch.
- Writer guidelines are implemented in-app.
- Submission validation is stronger across the app, admin flow, backend, and Google Form flow.

## Current Launch/Release State

- `testDebugUnitTest` passes after the redesign work.
- Release signing is configured locally with:
  - `keystore.properties`
  - `app/upload-keystore.jks`
- Internal testing was already set up in Play Console.
- A release bundle path exists at:
  - `app/build/outputs/bundle/release/app-release.aab`
- Important caveat:
  - the latest `bundleRelease` task timed out late in packaging, so the existing `.aab` may not include the very latest redesign changes from this session.
  - The file currently present has timestamp `2026-04-13 10:30:30 PM`.
  - Before uploading a new Play test build, generate and confirm a fresh `.aab`.

## Firebase / Auth / Backend

- Firebase Functions desktop submission endpoint is live:
  - `https://submitdesktopdvar-4ntfttmd6q-uc.a.run.app`
- Firestore rules and indexes were deployed successfully.
- Firebase Functions were upgraded and redeployed successfully:
  - runtime: `Node.js 22`
  - `firebase-admin`: `^13.8.0`
  - `firebase-functions`: `^7.2.5`
- Google sign-in flow was improved:
  - explicit Google sign-in button flow
  - retry after stale credential clearing
  - button moved above email/password
- The app side no longer relies on the older generic credential lookup flow.

## Welcome Email Setup

- Brevo was chosen for transactional onboarding emails.
- A backend writer-approval email trigger was added in:
  - `functions/index.js`
- It triggers from Firestore writer-application approval:
  - `writer_applications/{applicationId}`
- It sends only when an application transitions to `approved`.
- Local ignored env file now includes:
  - `BREVO_API_KEY`
  - `BREVO_SENDER_NAME`
  - `BREVO_SENDER_EMAIL`
- Additional env values for approval email links:
  - `BREVO_WRITER_FORM_URL`
  - `BREVO_WRITER_INSTRUCTIONS_URL`
- Brevo sender/domain setup was completed by the user:
  - sender: `no-reply@simchalapp.com`
- The writer approval email function was deployed successfully and is live.
- Current local config values:
  - `BREVO_SENDER_EMAIL=no-reply@simchalapp.com`
  - `BREVO_WRITER_FORM_URL=https://forms.gle/6fxqc3Cs73aTFeLU8`
  - `BREVO_WRITER_INSTRUCTIONS_URL=https://docs.google.com/document/d/1CZeG2rEpfX6OHArKpSdMwimFb_1TtD0qHcTuqN9LQI8/edit`
- Current writer approval email includes:
  - approval/onboarding copy
  - Google Form link
  - desktop submission instructions doc link
  - privacy policy link
  - account deletion link

## Google Form / Apps Script

- The Google Form submission flow now works.
- Apps Script was updated to use the real Form trigger event object shape.
- The script tolerates label variations and normalizes:
  - parsha names
  - Yom Tov names
  - special occasions
- The Form field can be titled:
  - `Parsha / Yomtov / Occasion`
- Form validation guidance already set:
  - email validation
  - body min length
  - body max length

## Validation Rules

- Body minimum: `40` characters
- Body maximum: `5000` characters
- Validation is enforced in:
  - app write flow
  - admin publishing flow
  - backend function
  - Google Form configuration

## Occasion Model

### Categories

- `Parsha`
- `Yom Tov`
- `Special Occasion`

### Added Yom Tov / Seasonal Keys

- `rosh_hashana`
- `aseres_yemei_teshuvah`
- `yom_kippur`
- `sukkot`
- `hoshana_rabba`
- `shemini_atzeret`
- `simchat_torah`
- `chanukah`
- `taanis_esther`
- `purim`
- `pesach`
- `leil_haseder`
- `shevii_shel_pesach`
- `sefirat_haomer`
- `lag_baomer`
- `shavuot`
- `seventeenth_of_tammuz`
- `three_weeks`
- `tisha_baav`
- `tu_baav`
- `tenth_of_tevet`

### Added Special Occasion Keys

- `bris_milah`
- `pidyon_haben`
- `engagement`
- `wedding`
- `sheva_berachot`
- `bar_mitzvah`
- `bat_mitzvah`

## Major UI / UX Redesign Completed

The app was redesigned to follow the Stitch export direction in `stitch_review/`.

### New Brand / Visual Foundation

- New launcher and Play icon now use:
  - `C:\Users\simch\Downloads\quick dvar torah icon.png`
- Theme updated to a parchment / ink editorial palette.
- Typography and shapes were updated to feel less default Material.
- Shared cards and panels were softened and made more editorial.
- Bottom navigation shell was redesigned.

### Screens Redone

- `Home / Feed`
  - stronger spotlight card
  - better section hierarchy
  - curated home feel
- `Browse`
  - new dedicated route
  - search
  - category cards
  - popular tags from real content
  - recent list
- `Saved`
  - new dedicated route
  - built on existing likes system
  - signed-out prompt
  - category filters
- `Write`
  - editorial intro
  - clearer sectioning
  - occasion quick chips + full dropdown
  - guidance card
  - bottom submit CTA
- `Detail / Reading`
  - stronger article header
  - better reading rhythm
  - reflection panel
- `Profile`
  - creator/account hub treatment
  - stronger identity header
  - stats row
  - cleaner grouping of account controls and submissions

## Files Most Relevant To The Current App State

### Core navigation

- `app/src/main/java/com/quickdvartorah/app/navigation/AppNavHost.kt`
- `app/src/main/java/com/quickdvartorah/app/navigation/Screen.kt`

### Theme / visual system

- `app/src/main/java/com/quickdvartorah/app/ui/theme/Color.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/theme/Theme.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/theme/Type.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/theme/Shape.kt`

### Redesigned screens

- `app/src/main/java/com/quickdvartorah/app/ui/feed/FeedScreen.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/browse/BrowseScreen.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/browse/BrowseViewModel.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/saved/SavedScreen.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/saved/SavedViewModel.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/detail/DvarTorahDetailScreen.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/write/WriteScreen.kt`
- `app/src/main/java/com/quickdvartorah/app/ui/profile/ProfileScreen.kt`

### Validation / submission flow

- `app/src/main/java/com/quickdvartorah/app/data/validation/SubmissionValidation.kt`
- `functions/index.js`
- `google_apps_script/submit_to_shabbosvorts.gs`

### Data model / occasions

- `app/src/main/java/com/quickdvartorah/app/data/model/ParshaOccasion.kt`
- `app/src/main/java/com/quickdvartorah/app/data/repository/DvarTorahRepository.kt`
- `app/src/main/java/com/quickdvartorah/app/data/repository/DvarTorahRepositoryImpl.kt`

## Biggest Remaining Items

### Before Play upload

1. Generate a confirmed fresh signed `.aab` after the redesign work.
2. Upload that fresh bundle to Internal testing.
3. Install from Play on a real device and test:
   - app launch
   - Google sign-in
   - feed/home
   - browse
   - saved
   - write
   - detail
   - profile
4. Fix any device-specific spacing/usability regressions found during testing.

### Backend cleanup

1. Optionally run one real writer-approval test to confirm Brevo delivery end to end.
2. Check Firebase logs if the first live approval email does not arrive.

### Play Console / release readiness

1. Finish store listing and screenshots.
2. Complete content rating and app content declarations.
3. Complete data safety.
4. Verify account deletion and privacy links in Play Console.
5. Submit through internal testing first, then move outward.

## Recommended Next Action

The next high-value step is:

1. finish a confirmed fresh `bundleRelease`
2. upload it to Play Internal testing
3. test the redesigned build on-device

That will surface the real final polish issues much faster than continuing blind code edits.
