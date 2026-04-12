package com.example.dvartorahapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.example.dvartorahapp.data.remote.FirestoreConstants

data class Report(
    @DocumentId val id: String = "",
    val dvarId: String = "",
    val reporterUid: String = "",
    val reporterName: String = "",
    val reporterEmail: String = "",
    val reason: String = "",
    val dvarTitle: String = "",
    val dvarAuthorUid: String = "",
    val dvarAuthorName: String = "",
    val dvarOccasion: String = "",
    val dvarBodyPreview: String = "",
    val dvarStatus: String = FirestoreConstants.DvarTorahStatus.PUBLISHED,
    val adminNote: String = "",
    val status: String = FirestoreConstants.ReportStatus.PENDING,
    val reviewedAt: Timestamp? = null,
    val reviewedBy: String = "",
    @ServerTimestamp val submittedAt: Timestamp? = null
)
