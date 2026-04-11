# Firebase Setup

This app uses one privilege field only:

- `users/{uid}.role = "viewer" | "writer" | "admin"`

Do not add boolean privilege flags such as `admin: true` or `writer: true`. The app ignores them.

## Required collections

- `users`
- `divrei_torah`
- `writer_applications`
- `reports`

`likes` is a subcollection under each `divrei_torah/{dvarId}` document.

## User document shape

Example `users/{uid}` document:

```json
{
  "displayName": "Tester One",
  "email": "tester@example.com",
  "role": "viewer",
  "profileImageUrl": null
}
```

## Bootstrap the first admin

The app cannot create the first admin account by itself.

1. Register normally in the app.
2. Open Firestore.
3. Find `users/{your_uid}`.
4. Set `role` to the exact string `admin`.
5. Sign out and sign back in.

## Deploy rules and indexes

From the project root:

```bash
firebase deploy --only firestore:rules,firestore:indexes
```

This repository now includes:

- `firestore.rules`
- `firestore.indexes.json`
- `firebase.json`

## Beta test checklist

1. Register a fresh account and confirm a `users/{uid}` profile document is created.
2. Confirm the new account starts with `role = "viewer"`.
3. Submit one writer application and confirm duplicate submissions are blocked.
4. Approve that application from an admin account.
5. Confirm the target `users/{uid}.role` changes to `writer`.
6. Sign out and back in on the writer account and confirm the `Write` tab appears.
7. Change one test user to `admin` by setting `role = "admin"` and confirm the `Admin` tab appears after re-login.
8. Create, view, like, and report a Dvar Torah with separate test accounts.

## Current MVP note

Admin approval is still performed directly by the Android client. That is acceptable for a small MVP only if Firestore rules are deployed and tested. For a harder security boundary later, move role changes into Cloud Functions or another trusted backend.
