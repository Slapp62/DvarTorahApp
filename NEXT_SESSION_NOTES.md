# Next Session Notes

Use `SESSION_HANDOFF_SUMMARY.md` as the canonical project handoff.

## Current Focus

- Prepare the current redesign/rebrand state for emulator and Play internal testing.
- Confirm a fresh signed release bundle before uploading another Play build.
- Run a real-device/emulator smoke test across launch, auth, feed, browse, saved, write, detail, profile, and admin flows.

## Known Current State

- App name: `Quick Dvar Torah`
- Android package: `com.quickdvartorah.app`
- Ads are disabled for MVP launch.
- Firebase Functions are deployed on Node.js 22.
- Desktop Google Form submission flow is live.
- Writer approval email via Brevo is live.
- Local debug build and unit tests pass.

## Next Best Commands

From repo root:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='C:\Users\simch\AndroidStudioProjects\DvarTorahApp\.gradle-local'
./gradlew.bat assembleDebug testDebugUnitTest --console=plain
```

For emulator install:

```powershell
./gradlew.bat installDebug --console=plain
```
