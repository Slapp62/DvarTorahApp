package com.quickdvartorah.app.data.validation

import com.quickdvartorah.app.data.model.ExternalSubmission
import com.quickdvartorah.app.data.model.ParshaOccasion

object SubmissionValidation {
    const val TITLE_MAX_LENGTH = 200
    const val BODY_MIN_LENGTH = 40
    const val BODY_MAX_LENGTH = 5_000
    const val SOURCES_MAX_LENGTH = 4_000
    const val DOCUMENT_URL_MAX_LENGTH = 2_000
    const val NAME_MAX_LENGTH = 120
    const val EMAIL_MAX_LENGTH = 200

    fun validateDraft(
        title: String,
        occasion: ParshaOccasion?,
        body: String,
        sources: String
    ): String? {
        val trimmedTitle = title.trim()
        val trimmedBody = body.trim()
        val trimmedSources = sources.trim()

        return when {
            trimmedTitle.isBlank() -> "Enter a title"
            trimmedTitle.length > TITLE_MAX_LENGTH -> "Title must be $TITLE_MAX_LENGTH characters or fewer"
            occasion == null -> "Select a parsha"
            trimmedBody.isBlank() -> "Write your Dvar Torah"
            trimmedBody.length < BODY_MIN_LENGTH -> "Body must be at least $BODY_MIN_LENGTH characters"
            trimmedBody.length > BODY_MAX_LENGTH -> "Body must be $BODY_MAX_LENGTH characters or fewer"
            trimmedSources.length > SOURCES_MAX_LENGTH -> "Sources must be $SOURCES_MAX_LENGTH characters or fewer"
            else -> null
        }
    }

    fun validateExternalSubmission(submission: ExternalSubmission): String? {
        val name = submission.submitterName.trim()
        val email = submission.submitterEmail.trim()
        val title = submission.title.trim()
        val body = submission.body.trim()
        val sources = submission.sources.trim()
        val documentUrl = submission.documentUrl.trim()

        return when {
            name.isBlank() -> "Submission is missing a name"
            name.length > NAME_MAX_LENGTH -> "Submitter name is too long"
            email.isBlank() || !email.contains("@") -> "Submission email is invalid"
            email.length > EMAIL_MAX_LENGTH -> "Submission email is too long"
            title.isBlank() -> "Submission title is missing"
            title.length > TITLE_MAX_LENGTH -> "Submission title is too long"
            submission.parshaOccasion == null -> "Submission parsha is invalid"
            body.length < BODY_MIN_LENGTH -> "Submission body is too short"
            body.length > BODY_MAX_LENGTH -> "Submission body is too long"
            sources.length > SOURCES_MAX_LENGTH -> "Submission sources are too long"
            documentUrl.length > DOCUMENT_URL_MAX_LENGTH -> "Submission Google Docs link is too long"
            else -> null
        }
    }
}
