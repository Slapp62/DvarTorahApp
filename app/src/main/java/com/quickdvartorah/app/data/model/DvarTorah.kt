package com.quickdvartorah.app.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class DvarTorah(
    @DocumentId val id: String = "",
    val title: String = "",
    val occasion: String = "",
    val authorUid: String = "",
    val authorName: String = "",
    val body: String = "",
    val sources: String = "",
    val status: String = "published",
    val likeCount: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    val parshaOccasion: ParshaOccasion? get() = ParshaOccasion.fromKey(occasion)
}
