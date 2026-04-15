# Play Store Readiness

This app now includes:

- Google sign-in through Firebase Authentication
- In-app account deletion
- Public privacy policy and account deletion URLs
- Writer onboarding, submission validation, and desktop submission review flow
- Ads disabled for the initial launch

## Must-Finish Before Submission

1. Firebase / Google sign-in release setup
- In Firebase Authentication, confirm the `Google` provider is enabled.
- In Firebase project settings, register the release SHA-1 and SHA-256 fingerprints for the Play signing key or upload key.
- Verify that `app/google-services.json` matches the production Firebase project.
- Test Google sign-in on a release-signed build before submitting.

2. Release packaging
- Build and install a release bundle or release APK from the same code you plan to submit.
- Upload an Android App Bundle (`.aab`) to Play Console.
- Confirm `versionCode` and `versionName` are correct for the first public release.
- Turn on Play App Signing.

3. Play Console declarations
- `Ads`: no, for the initial launch, because ads are disabled in the shipped app flow.
- `Data safety`: declare account email, display name, and user-generated content.
- `App content`: complete target audience and content rating questionnaires.
- `Account deletion`: provide the external deletion URL and describe what data is deleted.

4. Store listing assets
- Add screenshots, app icon, feature graphic, app description, category, and contact email.
- Verify the privacy policy URL and account deletion URL are the public live pages you want shown in Play Console.

5. Final policy / UX checks
- Keep the in-app delete-account entry point visible, since the app supports account creation.
- Do one end-to-end writer flow check:
  - sign in
  - open Write
  - publish a Dvar Torah
  - confirm it appears in the feed
- Do one end-to-end desktop submission flow check:
  - submit the Google Form
  - confirm it lands in `external_submissions`
  - review and publish it in admin

## Current Local Status

- `targetSdk = 36`
- `versionCode = 3`
- `versionName = 1.0.1`
- Ads are disabled in the app runtime and AdMob metadata is removed from the manifest.
- Android debug unit tests pass locally.

## Follow-Up After Launch

- Decide whether to keep the current package name or move away from `com.quickdvartorah.app` before wider scale distribution.
- If ads are reintroduced later, restore:
  - manifest AdMob metadata
  - banner placement
  - consent flow
  - privacy policy disclosures
  - Play Console ads/data safety declarations
