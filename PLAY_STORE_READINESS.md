# Play Store Readiness

This app now includes:

- Google sign-in through Firebase Authentication
- AdMob banner support with Google UMP consent handling
- In-app account deletion

Manual release tasks still required before submission:

1. Firebase / Google sign-in
- In Firebase Authentication, enable the `Google` provider.
- In Firebase project settings, register the release SHA-1 and SHA-256 fingerprints for the Play signing key or upload key.
- Verify that `app/google-services.json` matches the production Firebase project.

2. AdMob
- Replace the sample IDs in [strings.xml](app/src/main/res/values/strings.xml) with your real AdMob app ID and banner ad unit ID.
- Keep test ads enabled during development. The current IDs are Google sample test IDs.
- If you choose a target audience that includes children, update the ad request configuration before release to comply with Families policy requirements.

3. Privacy policy and account deletion URL
- Host a public privacy policy page and a public account deletion page.
- Replace the placeholder URLs in [strings.xml](app/src/main/res/values/strings.xml).
- The in-app deletion flow exists, but Play also requires an external web URL for account deletion support/disclosure.

4. Play Console declarations
- `Ads`: yes, because AdMob is integrated.
- `Data safety`: declare account email, display name, user-generated content, and any ad-related identifiers/data handled by the SDKs you enable.
- `App content`: complete target audience, ads declaration, and content rating questionnaires.
- `Account deletion`: provide the external deletion URL and explain what data is deleted.

5. Release packaging
- Upload an Android App Bundle (`.aab`), not just a debug APK.
- Set a real versionCode/versionName for release.
- Add app icon, screenshots, feature graphic, contact email, and category in Play Console.
- Turn on Play App Signing.

6. Policy checks
- Because the app supports account creation, keep the in-app delete-account entry point visible.
- If you serve ads in the EEA/UK, verify the consent form displays correctly on a clean install.
- If the app is directed to children or mixed audience, review the Families policy and set child-directed ad handling before loading ads.

Current policy notes verified on April 12, 2026 from official Google docs:

- Firebase’s Android Google sign-in docs recommend Credential Manager plus `googleid`.
- AdMob requires the manifest `com.google.android.gms.ads.APPLICATION_ID` metadata tag.
- Google Play’s target API policy requires new apps and updates to target a recent Android API level within one year of the latest major Android release.
- Apps with account creation need an in-app account deletion path and related Play Console disclosure.

Useful official docs:

- Firebase Google sign-in: https://firebase.google.com/docs/auth/android/google-signin
- AdMob quick start: https://developers.google.com/admob/android/quick-start
- AdMob banner ads: https://developers.google.com/admob/android/banner
- AdMob privacy / consent: https://developers.google.com/admob/android/privacy
- Google Play policy center: https://support.google.com/googleplay/android-developer/answer/16543315
- Google Play target API policy: https://support.google.com/googleplay/android-developer/answer/16561298
