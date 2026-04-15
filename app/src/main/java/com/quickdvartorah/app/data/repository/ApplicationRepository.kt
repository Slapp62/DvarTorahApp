package com.quickdvartorah.app.data.repository

import com.quickdvartorah.app.data.model.WriterApplication
import kotlinx.coroutines.flow.Flow

interface ApplicationRepository {
    fun getPendingApplications(): Flow<List<WriterApplication>>
    fun getUserApplication(uid: String): Flow<WriterApplication?>
    suspend fun submitApplication(application: WriterApplication): Result<Unit>
    suspend fun approveApplication(applicationId: String, applicantUid: String, reviewerUid: String): Result<Unit>
    suspend fun rejectApplication(applicationId: String, reviewerUid: String): Result<Unit>
}
