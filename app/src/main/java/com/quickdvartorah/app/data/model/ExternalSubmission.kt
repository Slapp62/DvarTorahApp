package com.quickdvartorah.app.data.model

import com.quickdvartorah.app.data.remote.FirestoreConstants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class ExternalSubmission(
    @DocumentId val id: String = "",
    val submitterName: String = "",
    val submitterEmail: String = "",
    val title: String = "",
    val occasion: String = "",
    val body: String = "",
    val sources: String = "",
    val documentUrl: String = "",
    val status: String = FirestoreConstants.ExternalSubmissionStatus.PENDING,
    @ServerTimestamp val submittedAt: Timestamp? = null,
    val reviewedAt: Timestamp? = null,
    val reviewedBy: String = "",
    val adminNote: String = "",
    val publishedDvarId: String = ""
) {
    val parshaOccasion: ParshaOccasion? get() = ParshaOccasion.fromKey(occasion)
}
