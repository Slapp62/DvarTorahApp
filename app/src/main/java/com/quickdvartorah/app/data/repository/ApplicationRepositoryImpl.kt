package com.quickdvartorah.app.data.repository

import com.quickdvartorah.app.data.model.WriterApplication
import com.quickdvartorah.app.data.remote.FirestoreConstants
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
class ApplicationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ApplicationRepository {

    private val collection get() = firestore.collection(FirestoreConstants.COLLECTION_WRITER_APPLICATIONS)
    private val usersCollection get() = firestore.collection(FirestoreConstants.COLLECTION_USERS)

    override fun getPendingApplications(): Flow<List<WriterApplication>> = callbackFlow {
        val listener = collection
            .whereEqualTo(FirestoreConstants.ApplicationFields.STATUS, FirestoreConstants.ApplicationStatus.PENDING)
            .orderBy(FirestoreConstants.ApplicationFields.SUBMITTED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(WriterApplication::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getUserApplication(uid: String): Flow<WriterApplication?> = callbackFlow {
        val listener = collection
            .whereEqualTo(FirestoreConstants.ApplicationFields.APPLICANT_UID, uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.firstOrNull()?.toObject(WriterApplication::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun submitApplication(application: WriterApplication): Result<Unit> = runCatching {
        collection.document(application.applicantUid).set(application).await()
    }

    override suspend fun approveApplication(
        applicationId: String,
        applicantUid: String,
        reviewerUid: String
    ): Result<Unit> = runCatching {
        val now = Timestamp.now()
        firestore.runBatch { batch ->
            batch.update(
                collection.document(applicationId),
                mapOf(
                    FirestoreConstants.ApplicationFields.STATUS to FirestoreConstants.ApplicationStatus.APPROVED,
                    FirestoreConstants.ApplicationFields.REVIEWED_AT to now,
                    FirestoreConstants.ApplicationFields.REVIEWED_BY to reviewerUid
                )
            )
            batch.update(
                usersCollection.document(applicantUid),
                FirestoreConstants.UserFields.ROLE, FirestoreConstants.Roles.WRITER
            )
        }.await()
    }

    override suspend fun rejectApplication(applicationId: String, reviewerUid: String): Result<Unit> = runCatching {
        collection.document(applicationId).update(
            mapOf(
                FirestoreConstants.ApplicationFields.STATUS to FirestoreConstants.ApplicationStatus.REJECTED,
                FirestoreConstants.ApplicationFields.REVIEWED_AT to Timestamp.now(),
                FirestoreConstants.ApplicationFields.REVIEWED_BY to reviewerUid
            )
        ).await()
    }
}
