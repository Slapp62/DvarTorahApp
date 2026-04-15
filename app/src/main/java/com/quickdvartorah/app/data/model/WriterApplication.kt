package com.quickdvartorah.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.quickdvartorah.app.data.remote.FirestoreConstants

data class WriterApplication(
    @DocumentId val id: String = "",
    val applicantUid: String = "",
    val applicantName: String = "",
    val applicantEmail: String = "",
    val motivation: String = "",
    val status: String = FirestoreConstants.ApplicationStatus.PENDING,
    @ServerTimestamp val submittedAt: Timestamp? = null,
    val reviewedAt: Timestamp? = null,
    val reviewedBy: String? = null
)
