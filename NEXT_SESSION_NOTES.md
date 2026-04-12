# Next Session Notes

## Current State

- Android app debug build is clean:
  - `assembleDebug` passes
  - `testDebugUnitTest` passes
- Firestore rules were deployed successfully for the new desktop submission admin flow.
- Desktop submission support has been added to the app:
  - new Firestore collection: `external_submissions`
  - admin `Desktop` tab for reviewing submissions
  - admin can publish, reject, save notes, and reopen submissions
- Google sign-in is configured and working on the app side.
- AdMob IDs are set to the real production IDs.
- In-app policy screens and public GitHub Pages policy URLs are in place.

## Google Drive Assets Already Created

- Submission Sheet:
  - `ShabbosVorts Writer Submissions`
  - https://docs.google.com/spreadsheets/d/1xNyCX0-wzF9WaUMbmxwcZr7naDDhFilswwdReK1IxVo/edit
- Instructions Doc:
  - `ShabbosVorts Writer Submission Instructions`
  - https://docs.google.com/document/d/1CZeG2rEpfX6OHArKpSdMwimFb_1TtD0qHcTuqN9LQI8/edit

## Repo Changes Added For Desktop Submission Flow

- App/admin support:
  - `app/src/main/java/com/example/dvartorahapp/data/model/ExternalSubmission.kt`
  - `app/src/main/java/com/example/dvartorahapp/data/repository/ExternalSubmissionRepository.kt`
  - `app/src/main/java/com/example/dvartorahapp/data/repository/ExternalSubmissionRepositoryImpl.kt`
  - `app/src/main/java/com/example/dvartorahapp/ui/admin/AdminViewModel.kt`
  - `app/src/main/java/com/example/dvartorahapp/ui/admin/AdminPanelScreen.kt`
- Firestore and repo support:
  - `app/src/main/java/com/example/dvartorahapp/data/remote/FirestoreConstants.kt`
  - `app/src/main/java/com/example/dvartorahapp/data/repository/DvarTorahRepository.kt`
  - `app/src/main/java/com/example/dvartorahapp/data/repository/DvarTorahRepositoryImpl.kt`
  - `firestore.rules`
  - `firebase.json`
- Writer/content-policy and launch prep already added:
  - `app/src/main/java/com/example/dvartorahapp/ui/apply/WriterApplicationScreen.kt`
  - `app/src/main/java/com/example/dvartorahapp/ui/apply/WriterApplicationViewModel.kt`
  - `PLAY_CONSOLE_SUBMISSION.md`
- Google Form / backend scaffolding:
  - `google_apps_script/submit_to_shabbosvorts.gs`
  - `functions/index.js`
  - `functions/package.json`
  - `DESKTOP_SUBMISSIONS_SETUP.md`

## Important Blockers

### 1. Firebase Functions deployment is not finished

Reason:
- project `dvartorahapp` is still on the `Spark` plan
- secure secret-backed function setup needs `Blaze`

Attempted command result:
- `firebase functions:secrets:set DESKTOP_SUBMISSION_SECRET`
- failed because Blaze is required

### 2. Google Form is not created yet

Manual work still needed:
- create the actual Google Form
- attach Apps Script to the Form
- fill in:
  - `YOUR_FUNCTION_URL_HERE`
  - `YOUR_SHARED_SECRET_HERE`

## Recommended Next Steps

1. Upgrade Firebase project `dvartorahapp` to `Blaze`.
2. Deploy Functions backend for desktop submissions.
3. Create the Google Form that matches the field names in:
   - `DESKTOP_SUBMISSIONS_SETUP.md`
   - `google_apps_script/submit_to_shabbosvorts.gs`
4. Paste the Apps Script into the Form project.
5. Set the function URL and shared secret.
6. Test one end-to-end desktop submission into `external_submissions`.
7. Review and publish that submission in the app admin panel.

## Useful Commands

From repo root:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
$env:GRADLE_USER_HOME='C:\Users\simch\AndroidStudioProjects\DvarTorahApp\.gradle-local'
$env:ANDROID_SDK_HOME='C:\Users\simch\AndroidStudioProjects\DvarTorahApp\.android-local'
$env:KOTLIN_DAEMON_DIR='C:\Users\simch\AndroidStudioProjects\DvarTorahApp\.kotlin-daemon'
./gradlew.bat assembleDebug testDebugUnitTest --console=plain
```

Deploy Firestore rules:

```powershell
npx firebase-tools deploy --only firestore:rules --project dvartorahapp
```

Functions work will begin from:

```powershell
cd functions
npm install
```

## Git Status Goal For Next Start

Before ending this session, commit and push all current local changes so the repo is clean and synced with GitHub.
