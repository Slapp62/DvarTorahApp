package com.quickdvartorah.app.data.repository

import com.quickdvartorah.app.data.model.Report
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
class ReportRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReportRepository {

    private val collection get() = firestore.collection(FirestoreConstants.COLLECTION_REPORTS)

    override fun getPendingReports(): Flow<List<Report>> = callbackFlow {
        val listener = collection
            .whereEqualTo(FirestoreConstants.ReportFields.STATUS, FirestoreConstants.ReportStatus.PENDING)
            .orderBy(FirestoreConstants.ReportFields.SUBMITTED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(Report::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override fun getReviewedReports(): Flow<List<Report>> = callbackFlow {
        val listener = collection
            .whereIn(
                FirestoreConstants.ReportFields.STATUS,
                listOf(FirestoreConstants.ReportStatus.ACTIONED, FirestoreConstants.ReportStatus.DISMISSED)
            )
            .orderBy(FirestoreConstants.ReportFields.REVIEWED_AT, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(Report::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun submitReport(report: Report): Result<Unit> = runCatching {
        collection.add(report).await()
    }

    override suspend fun reviewReport(
        reportId: String,
        status: String,
        reviewedBy: String,
        adminNote: String
    ): Result<Unit> = runCatching {
        collection.document(reportId).update(
            mapOf(
                FirestoreConstants.ReportFields.STATUS to status,
                FirestoreConstants.ReportFields.REVIEWED_BY to reviewedBy,
                FirestoreConstants.ReportFields.REVIEWED_AT to Timestamp.now(),
                FirestoreConstants.ReportFields.ADMIN_NOTE to adminNote.trim()
            )
        ).await()
    }

    override suspend fun updateReportedDvarStatus(reportId: String, dvarStatus: String): Result<Unit> = runCatching {
        collection.document(reportId).update(
            FirestoreConstants.ReportFields.DVAR_STATUS,
            dvarStatus
        ).await()
    }

    override suspend fun updateAdminNote(reportId: String, adminNote: String): Result<Unit> = runCatching {
        collection.document(reportId).update(
            FirestoreConstants.ReportFields.ADMIN_NOTE,
            adminNote.trim()
        ).await()
    }

    override suspend fun reopenReport(reportId: String): Result<Unit> = runCatching {
        collection.document(reportId).update(
            mapOf(
                FirestoreConstants.ReportFields.STATUS to FirestoreConstants.ReportStatus.PENDING,
                FirestoreConstants.ReportFields.REVIEWED_BY to "",
                FirestoreConstants.ReportFields.REVIEWED_AT to null
            )
        ).await()
    }
}
