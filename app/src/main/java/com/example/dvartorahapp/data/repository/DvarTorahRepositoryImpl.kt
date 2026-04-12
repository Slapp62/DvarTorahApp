package com.example.dvartorahapp.data.repository

import com.example.dvartorahapp.data.model.DvarTorah
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DvarTorahRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DvarTorahRepository {

    private val collection get() = firestore.collection(FirestoreConstants.COLLECTION_DIVREI_TORAH)

    override fun getPublishedDvareiTorah(occasionFilter: String?): Flow<List<DvarTorah>> = callbackFlow {
        var query = collection
            .whereEqualTo(FirestoreConstants.DvarTorahFields.STATUS, FirestoreConstants.DvarTorahStatus.PUBLISHED)
            .orderBy(FirestoreConstants.DvarTorahFields.CREATED_AT, Query.Direction.DESCENDING)

        if (occasionFilter != null) {
            query = query.whereEqualTo(FirestoreConstants.DvarTorahFields.OCCASION, occasionFilter)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(kotlinx.coroutines.CancellationException(error.message ?: "Firestore error", error))
                return@addSnapshotListener
            }
            val items = snapshot?.toObjects(DvarTorah::class.java) ?: emptyList()
            trySend(items)
        }
        awaitClose { listener.remove() }
    }

    override fun getDvarTorahById(dvarId: String): Flow<DvarTorah?> = callbackFlow {
        val listener = collection.document(dvarId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(DvarTorah::class.java))
        }
        awaitClose { listener.remove() }
    }

    override fun getUserDvareiTorah(authorUid: String): Flow<List<DvarTorah>> = callbackFlow {
        val listener = collection
            .whereEqualTo(FirestoreConstants.DvarTorahFields.AUTHOR_UID, authorUid)
            .orderBy(FirestoreConstants.DvarTorahFields.CREATED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(DvarTorah::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createDvarTorah(dvarTorah: DvarTorah): Result<String> = runCatching {
        val ref = collection.document()
        val withId = dvarTorah.copy(id = ref.id)
        ref.set(withId).await()
        ref.id
    }

    override suspend fun createAdminPublishedDvarTorah(
        title: String,
        occasion: String,
        authorName: String,
        authorUid: String,
        body: String,
        sources: String
    ): Result<String> = runCatching {
        val ref = collection.document()
        ref.set(
            DvarTorah(
                id = ref.id,
                title = title.trim(),
                occasion = occasion,
                authorUid = authorUid,
                authorName = authorName.trim(),
                body = body.trim(),
                sources = sources.trim(),
                status = FirestoreConstants.DvarTorahStatus.PUBLISHED
            )
        ).await()
        ref.id
    }

    override suspend fun updateDvarTorah(dvarTorah: DvarTorah): Result<Unit> = runCatching {
        val updates = mapOf(
            FirestoreConstants.DvarTorahFields.TITLE to dvarTorah.title,
            FirestoreConstants.DvarTorahFields.OCCASION to dvarTorah.occasion,
            FirestoreConstants.DvarTorahFields.BODY to dvarTorah.body,
            FirestoreConstants.DvarTorahFields.SOURCES to dvarTorah.sources,
            FirestoreConstants.DvarTorahFields.UPDATED_AT to Timestamp.now()
        )
        collection.document(dvarTorah.id).update(updates).await()
    }

    override suspend fun toggleLike(dvarId: String, uid: String): Result<Unit> = runCatching {
        val dvarRef = collection.document(dvarId)
        val likeRef = dvarRef.collection(FirestoreConstants.COLLECTION_LIKES).document(uid)

        firestore.runTransaction { transaction ->
            val likeSnapshot = transaction.get(likeRef)
            val dvarSnapshot = transaction.get(dvarRef)
            val currentCount = dvarSnapshot.getLong(FirestoreConstants.DvarTorahFields.LIKE_COUNT) ?: 0

            if (likeSnapshot.exists()) {
                transaction.delete(likeRef)
                transaction.update(dvarRef, FirestoreConstants.DvarTorahFields.LIKE_COUNT, maxOf(0, currentCount - 1))
            } else {
                transaction.set(likeRef, mapOf("uid" to uid, "likedAt" to Timestamp.now()))
                transaction.update(dvarRef, FirestoreConstants.DvarTorahFields.LIKE_COUNT, currentCount + 1)
            }
        }.await()
    }

    override fun getUserLikedStatus(dvarId: String, uid: String): Flow<Boolean> = callbackFlow {
        val likeRef = collection.document(dvarId)
            .collection(FirestoreConstants.COLLECTION_LIKES)
            .document(uid)
        val listener = likeRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.exists() == true)
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateDvarTorahStatus(dvarId: String, status: String): Result<Unit> = runCatching {
        collection.document(dvarId)
            .update(FirestoreConstants.DvarTorahFields.STATUS, status)
            .await()
    }
}
