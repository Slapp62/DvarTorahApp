package com.example.dvartorahapp.data.repository

import com.example.dvartorahapp.data.model.Report
import com.example.dvartorahapp.data.remote.FirestoreConstants
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
                if (error != null) { close(error); return@addSnapshotListener }
                trySend(snapshot?.toObjects(Report::class.java) ?: emptyList())
            }
        awaitClose { listener.remove() }
    }

    override suspend fun submitReport(report: Report): Result<Unit> = runCatching {
        collection.add(report).await()
    }

    override suspend fun updateReportStatus(reportId: String, status: String): Result<Unit> = runCatching {
        collection.document(reportId).update(FirestoreConstants.ReportFields.STATUS, status).await()
    }
}
