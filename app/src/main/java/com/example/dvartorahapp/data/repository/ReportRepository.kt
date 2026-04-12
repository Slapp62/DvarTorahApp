package com.example.dvartorahapp.data.repository

import com.example.dvartorahapp.data.model.Report
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getPendingReports(): Flow<List<Report>>
    fun getReviewedReports(): Flow<List<Report>>
    suspend fun submitReport(report: Report): Result<Unit>
    suspend fun reviewReport(reportId: String, status: String, reviewedBy: String, adminNote: String = ""): Result<Unit>
    suspend fun updateReportedDvarStatus(reportId: String, dvarStatus: String): Result<Unit>
    suspend fun updateAdminNote(reportId: String, adminNote: String): Result<Unit>
    suspend fun reopenReport(reportId: String): Result<Unit>
}
