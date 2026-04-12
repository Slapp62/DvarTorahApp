package com.example.dvartorahapp.data.repository

import com.example.dvartorahapp.data.model.ExternalSubmission
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
class ExternalSubmissionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ExternalSubmissionRepository {

    private val collection get() = firestore.collection(FirestoreConstants.COLLECTION_EXTERNAL_SUBMISSIONS)

    override fun getPendingSubmissions(): Flow<List<ExternalSubmission>> = callbackFlow {
        val listener = collection
            .whereEqualTo(
                FirestoreConstants.ExternalSubmissionFields.STATUS,
                FirestoreConstants.ExternalSubmissionStatus.PENDING
            )
            .orderBy(FirestoreConstants.ExternalSubmissionFields.SUBMITTED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(ExternalSubmission::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getReviewedSubmissions(): Flow<List<ExternalSubmission>> = callbackFlow {
        val listener = collection
            .whereIn(
                FirestoreConstants.ExternalSubmissionFields.STATUS,
                listOf(
                    FirestoreConstants.ExternalSubmissionStatus.PUBLISHED,
                    FirestoreConstants.ExternalSubmissionStatus.REJECTED
                )
            )
            .orderBy(FirestoreConstants.ExternalSubmissionFields.REVIEWED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(ExternalSubmission::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun markPublished(
        submissionId: String,
        reviewerUid: String,
        publishedDvarId: String,
        adminNote: String
    ): Result<Unit> = runCatching {
        collection.document(submissionId).update(
            mapOf(
                FirestoreConstants.ExternalSubmissionFields.STATUS to FirestoreConstants.ExternalSubmissionStatus.PUBLISHED,
                FirestoreConstants.ExternalSubmissionFields.REVIEWED_BY to reviewerUid,
                FirestoreConstants.ExternalSubmissionFields.REVIEWED_AT to Timestamp.now(),
                FirestoreConstants.ExternalSubmissionFields.ADMIN_NOTE to adminNote.trim(),
                FirestoreConstants.ExternalSubmissionFields.PUBLISHED_DVAR_ID to publishedDvarId
            )
        ).await()
    }

    override suspend fun rejectSubmission(
        submissionId: String,
        reviewerUid: String,
        adminNote: String
    ): Result<Unit> = runCatching {
        collection.document(submissionId).update(
            mapOf(
                FirestoreConstants.ExternalSubmissionFields.STATUS to FirestoreConstants.ExternalSubmissionStatus.REJECTED,
                FirestoreConstants.ExternalSubmissionFields.REVIEWED_BY to reviewerUid,
                FirestoreConstants.ExternalSubmissionFields.REVIEWED_AT to Timestamp.now(),
                FirestoreConstants.ExternalSubmissionFields.ADMIN_NOTE to adminNote.trim()
            )
        ).await()
    }

    override suspend fun updateAdminNote(submissionId: String, adminNote: String): Result<Unit> = runCatching {
        collection.document(submissionId).update(
            FirestoreConstants.ExternalSubmissionFields.ADMIN_NOTE,
            adminNote.trim()
        ).await()
    }

    override suspend fun reopenSubmission(submissionId: String): Result<Unit> = runCatching {
        collection.document(submissionId).update(
            mapOf(
                FirestoreConstants.ExternalSubmissionFields.STATUS to FirestoreConstants.ExternalSubmissionStatus.PENDING,
                FirestoreConstants.ExternalSubmissionFields.REVIEWED_BY to "",
                FirestoreConstants.ExternalSubmissionFields.REVIEWED_AT to null,
                FirestoreConstants.ExternalSubmissionFields.PUBLISHED_DVAR_ID to ""
            )
        ).await()
    }
}
