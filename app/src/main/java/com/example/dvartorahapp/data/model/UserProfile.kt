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
    val viewer: Boolean = false,
    val writer: Boolean = false,
    val admin: Boolean = false,
    val profileImageUrl: String? = null,
    @ServerTimestamp val createdAt: Timestamp? = null
) {
    val effectiveRole: String
        get() = when {
            role == FirestoreConstants.Roles.ADMIN || admin -> FirestoreConstants.Roles.ADMIN
            role == FirestoreConstants.Roles.WRITER || writer -> FirestoreConstants.Roles.WRITER
            else -> FirestoreConstants.Roles.VIEWER
        }

    val isWriter: Boolean get() = effectiveRole == FirestoreConstants.Roles.WRITER || effectiveRole == FirestoreConstants.Roles.ADMIN
    val isAdmin: Boolean get() = effectiveRole == FirestoreConstants.Roles.ADMIN
    val isViewer: Boolean get() = effectiveRole == FirestoreConstants.Roles.VIEWER
}
