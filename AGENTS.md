# Repository Guidelines

## Project Structure & Module Organization
`app/` contains the Android app (`com.quickdvartorah.app`) built with Kotlin, Jetpack Compose, Hilt, and Firebase.
- App code: `app/src/main/java/com/quickdvartorah/app/...`
- Resources/assets: `app/src/main/res/...`
- Unit tests: `app/src/test/...`
- Instrumented tests: `app/src/androidTest/...`

Backend and integration assets are separate:
- Firebase Cloud Functions: `functions/index.js`
- Firestore config: `firestore.rules`, `firestore.indexes.json`, `firebase.json`
- Google Apps Script bridge: `google_apps_script/submit_to_shabbosvorts.gs`
- Policy/docs: `docs/`

## Build, Test, and Development Commands
Run from repo root:
- `./gradlew.bat assembleDebug` builds a debug APK.
- `./gradlew.bat testDebugUnitTest` runs local JVM unit tests.
- `./gradlew.bat connectedDebugAndroidTest` runs device/emulator tests.
- `./gradlew.bat bundleRelease` builds release AAB (`app/build/outputs/bundle/release/`).
- `./gradlew.bat signingReport` prints SHA fingerprints for Firebase/Auth setup.

Functions:
- `cd functions && npm install`
- `npx firebase-tools deploy --only functions --project dvartorahapp`

## Coding Style & Naming Conventions
Use Kotlin style defaults: 4-space indentation, no tabs, clear immutable-first code (`val` before `var`).
- Classes/screens/viewmodels: `PascalCase` (`FeedScreen`, `AuthRepositoryImpl`)
- Functions/properties: `camelCase`
- Packages: lowercase dot notation (`ui.feed`, `data.repository`)
- Compose files should keep preview/UI concerns local and push state/business logic to ViewModels/repositories.

## Testing Guidelines
Prefer fast unit tests in `app/src/test` for model/validation/repository logic, and reserve instrumented tests for Android framework behavior.
- Test names should describe behavior, e.g. `UserProfileTest`, `submission_with_short_body_fails`.
- Before PR: run `testDebugUnitTest`; run `connectedDebugAndroidTest` when UI/navigation/auth flows change.

## Commit & Pull Request Guidelines
Recent history uses short imperative commit subjects (e.g. `Add ...`, `Fix ...`, `Update ...`). Keep that format and scope each commit to one logical change.

PRs should include:
- What changed and why
- Risk/rollback notes (auth, Firebase config, release signing, rules)
- Verification steps and commands run
- Screenshots/video for UI changes (feed, browse, write, profile, auth)

## Security & Configuration Tips
Never commit secrets or local machine files. Keep `keystore.properties`, `local.properties`, and credential tokens local. When Google sign-in changes, refresh `app/google-services.json` and verify SHA fingerprints with `signingReport`.
