package com.example.dvartorahapp.data.repository

import com.example.dvartorahapp.data.model.Report
import kotlinx.coroutines.flow.Flow

interface ReportRepository {
    fun getPendingReports(): Flow<List<Report>>
    suspend fun submitReport(report: Report): Result<Unit>
    suspend fun updateReportStatus(reportId: String, status: String): Result<Unit>
}
