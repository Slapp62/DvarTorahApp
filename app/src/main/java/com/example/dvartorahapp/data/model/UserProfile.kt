package com.example.dvartorahapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import com.example.dvartorahapp.data.remote.FirestoreConstants

data class UserProfile(
    @DocumentId val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = FirestoreConstants.Roles.VIEWER,
    val profileImageUrl: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    val isWriter: Boolean get() = role == FirestoreConstants.Roles.WRITER || role == FirestoreConstants.Roles.ADMIN
    val isAdmin: Boolean get() = role == FirestoreConstants.Roles.ADMIN
    val isViewer: Boolean get() = role == FirestoreConstants.Roles.VIEWER
}
