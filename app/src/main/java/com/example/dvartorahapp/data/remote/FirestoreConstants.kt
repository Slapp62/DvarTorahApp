package com.example.dvartorahapp.data.remote

object FirestoreConstants {
    const val COLLECTION_USERS = "users"
    const val COLLECTION_DIVREI_TORAH = "divrei_torah"
    const val COLLECTION_LIKES = "likes"
    const val COLLECTION_WRITER_APPLICATIONS = "writer_applications"
    const val COLLECTION_REPORTS = "reports"

    object UserFields {
        const val DISPLAY_NAME = "displayName"
        const val EMAIL = "email"
        const val ROLE = "role"
        const val PROFILE_IMAGE_URL = "profileImageUrl"
        const val CREATED_AT = "createdAt"
    }

    object DvarTorahFields {
        const val TITLE = "title"
        const val OCCASION = "occasion"
        const val AUTHOR_UID = "authorUid"
        const val AUTHOR_NAME = "authorName"
        const val BODY = "body"
        const val SOURCES = "sources"
        const val STATUS = "status"
        const val LIKE_COUNT = "likeCount"
        const val CREATED_AT = "createdAt"
        const val UPDATED_AT = "updatedAt"
    }

    object ApplicationFields {
        const val APPLICANT_UID = "applicantUid"
        const val STATUS = "status"
        const val SUBMITTED_AT = "submittedAt"
        const val REVIEWED_AT = "reviewedAt"
        const val REVIEWED_BY = "reviewedBy"
    }

    object ReportFields {
        const val DVAR_ID = "dvarId"
        const val REPORTER_UID = "reporterUid"
        const val REPORTER_NAME = "reporterName"
        const val REPORTER_EMAIL = "reporterEmail"
        const val DVAR_TITLE = "dvarTitle"
        const val DVAR_AUTHOR_UID = "dvarAuthorUid"
        const val DVAR_AUTHOR_NAME = "dvarAuthorName"
        const val DVAR_OCCASION = "dvarOccasion"
        const val DVAR_BODY_PREVIEW = "dvarBodyPreview"
        const val DVAR_STATUS = "dvarStatus"
        const val ADMIN_NOTE = "adminNote"
        const val REVIEWED_AT = "reviewedAt"
        const val REVIEWED_BY = "reviewedBy"
        const val STATUS = "status"
        const val SUBMITTED_AT = "submittedAt"
    }

    object Roles {
        const val VIEWER = "viewer"
        const val WRITER = "writer"
        const val ADMIN = "admin"
    }

    object DvarTorahStatus {
        const val PUBLISHED = "published"
        const val FLAGGED = "flagged"
        const val REMOVED = "removed"
    }

    object ApplicationStatus {
        const val PENDING = "pending"
        const val APPROVED = "approved"
        const val REJECTED = "rejected"
    }

    object ReportStatus {
        const val PENDING = "pending"
        const val ACTIONED = "actioned"
        const val DISMISSED = "dismissed"
    }
}
