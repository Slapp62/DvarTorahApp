package com.quickdvartorah.app.data.repository

import com.quickdvartorah.app.data.model.ExternalSubmission
import kotlinx.coroutines.flow.Flow

interface ExternalSubmissionRepository {
    fun getPendingSubmissions(): Flow<List<ExternalSubmission>>
    fun getReviewedSubmissions(): Flow<List<ExternalSubmission>>
    suspend fun markPublished(
        submissionId: String,
        reviewerUid: String,
        publishedDvarId: String,
        adminNote: String
    ): Result<Unit>
    suspend fun rejectSubmission(
        submissionId: String,
        reviewerUid: String,
        adminNote: String
    ): Result<Unit>
    suspend fun updateAdminNote(submissionId: String, adminNote: String): Result<Unit>
    suspend fun reopenSubmission(submissionId: String): Result<Unit>
}
