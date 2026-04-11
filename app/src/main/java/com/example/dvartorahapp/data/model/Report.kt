package com.example.dvartorahapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.example.dvartorahapp.data.remote.FirestoreConstants

data class Report(
    @DocumentId val id: String = "",
    val dvarId: String = "",
    val reporterUid: String = "",
    val reason: String = "",
    val status: String = FirestoreConstants.ReportStatus.PENDING,
    @ServerTimestamp val submittedAt: Timestamp? = null
)
