package com.example.dvartorahapp.ui.policy

object PolicyContent {
    val privacySections = listOf(
        PolicySection(
            heading = "Information We Collect",
            body = listOf(
                "We may collect account information such as your name, email address, and sign-in details.",
                "We may collect user content such as Divrei Torah you submit, writer applications, reports, likes, and profile details.",
                "We may collect technical, consent, and ad-related information when needed to operate the app, improve reliability, and serve ads."
            )
        ),
        PolicySection(
            heading = "How We Use Information",
            body = listOf(
                "We use information to create and manage accounts, support browsing and publishing, review applications, moderate reports, improve app performance, and comply with legal obligations.",
                "We may also use data to display ads and manage ad consent through Google services where applicable."
            )
        ),
        PolicySection(
            heading = "Third-Party Services",
            body = listOf(
                "ShabbosVorts uses services such as Firebase Authentication, Cloud Firestore, Firebase Storage, Google AdMob, and Google User Messaging Platform.",
                "These providers may process data according to their own privacy terms."
            )
        ),
        PolicySection(
            heading = "Your Choices",
            body = listOf(
                "You may sign out, manage ad privacy choices where available, and delete your account through the app.",
                "If you have questions about privacy, contact the support email listed in the public privacy policy page."
            )
        )
    )

    val accountDeletionSections = listOf(
        PolicySection(
            heading = "How to Delete Your Account",
            body = listOf(
                "Open Profile and tap Delete account.",
                "You will be asked to confirm before deletion starts."
            )
        ),
        PolicySection(
            heading = "What Will Be Deleted",
            body = listOf(
                "Deleting your account removes your user profile, writer application, submitted reports, likes, and authored Divrei Torah stored in the app."
            )
        ),
        PolicySection(
            heading = "What May Be Retained",
            body = listOf(
                "We may retain limited information where necessary for legal compliance, security, fraud prevention, abuse handling, or dispute resolution."
            )
        ),
        PolicySection(
            heading = "If Deletion Does Not Complete",
            body = listOf(
                "Some deletion requests may require a recent sign-in session. If deletion fails, sign in again and retry."
            )
        )
    )

    val contentPolicySections = listOf(
        PolicySection(
            heading = "What Belongs on ShabbosVorts",
            body = listOf(
                "ShabbosVorts is for Divrei Torah, parsha thoughts, yom tov insights, and respectful Torah learning content.",
                "Submissions may reflect different hashkafic styles, but the main purpose must remain Torah learning rather than agitation or ideology."
            )
        ),
        PolicySection(
            heading = "Not Allowed",
            body = listOf(
                "Extremist content, political propaganda, incitement, glorification of violence, hate speech, harassment, threats, spam, or obscene material are not allowed.",
                "Content whose main purpose is provocation, recruitment, manipulation, or agenda-pushing rather than Torah is also not allowed."
            )
        ),
        PolicySection(
            heading = "Borderline Content",
            body = listOf(
                "Current-events commentary, highly polemical writing, conspiracy-style material, or content quoting offensive material may be reviewed manually.",
                "Administrators may flag or remove content that does not fit the purpose of the app."
            )
        ),
        PolicySection(
            heading = "Moderation",
            body = listOf(
                "Users may report content for review. Administrators may dismiss reports, flag content, remove content, restore content, or restrict writer access for repeated violations."
            )
        )
    )
}
