# Desktop Submission Setup

This app now supports a Firestore collection for desktop-written submissions:

- Collection: `external_submissions`
- Pending items appear in the admin panel under `Desktop`
- Admin can:
  - open the attached Google Doc link
  - publish the submission into `divrei_torah`
  - reject it
  - save admin notes
  - reopen reviewed items

## Suggested Google Form fields

Create these fields in Google Forms:

1. `Name`
2. `Email`
3. `Title`
4. `Parsha key`
5. `Body`
6. `Sources`
7. `Google Docs link`
8. `I agree to the content policy`

Use the parsha key values already used in the app, for example:

- `bereishit`
- `noach`
- `lech_lecha`

## Firestore document shape

Each form submission should create a document in `external_submissions` like:

```json
{
  "submitterName": "Writer Name",
  "submitterEmail": "writer@example.com",
  "title": "Sample title",
  "occasion": "bereishit",
  "body": "Full Dvar Torah text",
  "sources": "Rashi on Bereishit 1:1",
  "documentUrl": "https://docs.google.com/document/d/...",
  "contentPolicyAgreed": true,
  "status": "pending",
  "submittedAt": "<server timestamp>",
  "reviewedAt": null,
  "reviewedBy": "",
  "adminNote": "",
  "publishedDvarId": ""
}
```

## Recommended automation path

1. Google Form collects the submission.
2. Google Apps Script runs on form submit.
3. Apps Script posts to the Firebase function `submitDesktopDvar`.
4. The function writes the document above into Firestore.
5. Admin reviews and publishes inside the app.

## Security note

Do not open client-side public writes to `external_submissions`.
Use a trusted server path. This repo now includes:

- `functions/index.js` for a secure Firebase HTTP endpoint
- `google_apps_script/submit_to_shabbosvorts.gs` for the Google Apps Script trigger

## What still needs to be done

1. Run `npm install` inside `functions/`.
2. Set `DESKTOP_SUBMISSION_SECRET` for the function.
   Example:
   `firebase functions:secrets:set DESKTOP_SUBMISSION_SECRET`
3. Deploy the function.
   Example:
   `firebase deploy --only functions`
4. Create the Google Form with the listed field names.
5. Paste `google_apps_script/submit_to_shabbosvorts.gs` into Apps Script.
6. Add an installable `onFormSubmit` trigger.
7. Make sure the form response value for `I agree to the content policy` is a clear affirmative choice such as `Yes`.
8. Fill in:
   - `YOUR_FUNCTION_URL_HERE`
   - `YOUR_SHARED_SECRET_HERE`
