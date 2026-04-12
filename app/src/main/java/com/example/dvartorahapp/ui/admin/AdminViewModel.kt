package com.example.dvartorahapp.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dvartorahapp.data.model.Report
import com.example.dvartorahapp.data.model.WriterApplication
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.data.repository.ApplicationRepository
import com.example.dvartorahapp.data.repository.DvarTorahRepository
import com.example.dvartorahapp.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AdminUiEffect {
    data class ShowMessage(val message: String) : AdminUiEffect()
}

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val reportRepository: ReportRepository,
    private val dvarTorahRepository: DvarTorahRepository
) : ViewModel() {

    val pendingApplications: StateFlow<List<WriterApplication>> = applicationRepository
        .getPendingApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingReports: StateFlow<List<Report>> = reportRepository
        .getPendingReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val reviewedReports: StateFlow<List<Report>> = reportRepository
        .getReviewedReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _effect = Channel<AdminUiEffect>()
    val effect = _effect.receiveAsFlow()

    fun approveApplication(application: WriterApplication, reviewerUid: String) {
        viewModelScope.launch {
            applicationRepository.approveApplication(application.id, application.applicantUid, reviewerUid).fold(
                onSuccess = { _effect.send(AdminUiEffect.ShowMessage("${application.applicantName} approved")) },
                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not approve application: ${it.message}")) }
            )
        }
    }

    fun rejectApplication(application: WriterApplication, reviewerUid: String) {
        viewModelScope.launch {
            applicationRepository.rejectApplication(application.id, reviewerUid).fold(
                onSuccess = { _effect.send(AdminUiEffect.ShowMessage("Application rejected")) },
                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not reject application: ${it.message}")) }
            )
        }
    }

    fun flagContent(report: Report, reviewerUid: String, adminNote: String) {
        viewModelScope.launch {
            dvarTorahRepository.updateDvarTorahStatus(report.dvarId, FirestoreConstants.DvarTorahStatus.FLAGGED)
                .fold(
                    onSuccess = {
                        reportRepository.updateReportedDvarStatus(report.id, FirestoreConstants.DvarTorahStatus.FLAGGED)
                            .fold(
                                onSuccess = {
                                    reportRepository.reviewReport(report.id, FirestoreConstants.ReportStatus.ACTIONED, reviewerUid, adminNote)
                                        .fold(
                                            onSuccess = { _effect.send(AdminUiEffect.ShowMessage("Dvar Torah flagged")) },
                                            onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not update report: ${it.message}")) }
                                        )
                                },
                                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not update report: ${it.message}")) }
                            )
                    },
                    onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not flag Dvar Torah: ${it.message}")) }
                )
        }
    }

    fun removeContent(report: Report, reviewerUid: String, adminNote: String) {
        viewModelScope.launch {
            dvarTorahRepository.updateDvarTorahStatus(report.dvarId, FirestoreConstants.DvarTorahStatus.REMOVED)
                .fold(
                    onSuccess = {
                        reportRepository.updateReportedDvarStatus(report.id, FirestoreConstants.DvarTorahStatus.REMOVED)
                            .fold(
                                onSuccess = {
                                    reportRepository.reviewReport(report.id, FirestoreConstants.ReportStatus.ACTIONED, reviewerUid, adminNote)
                                        .fold(
                                            onSuccess = { _effect.send(AdminUiEffect.ShowMessage("Dvar Torah removed")) },
                                            onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not update report: ${it.message}")) }
                                        )
                                },
                                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not update report: ${it.message}")) }
                            )
                    },
                    onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not remove Dvar Torah: ${it.message}")) }
                )
        }
    }

    fun dismissReport(report: Report, reviewerUid: String, adminNote: String) {
        viewModelScope.launch {
            reportRepository.reviewReport(report.id, FirestoreConstants.ReportStatus.DISMISSED, reviewerUid, adminNote).fold(
                onSuccess = { _effect.send(AdminUiEffect.ShowMessage("Report dismissed")) },
                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not dismiss report: ${it.message}")) }
            )
        }
    }

    fun restoreContent(report: Report, reviewerUid: String, adminNote: String) {
        viewModelScope.launch {
            dvarTorahRepository.updateDvarTorahStatus(report.dvarId, FirestoreConstants.DvarTorahStatus.PUBLISHED)
                .fold(
                    onSuccess = {
                        reportRepository.updateReportedDvarStatus(report.id, FirestoreConstants.DvarTorahStatus.PUBLISHED)
                            .fold(
                                onSuccess = {
                                    reportRepository.reviewReport(report.id, FirestoreConstants.ReportStatus.ACTIONED, reviewerUid, adminNote)
                                        .fold(
                                            onSuccess = { _effect.send(AdminUiEffect.ShowMessage("Dvar Torah restored")) },
                                            onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not update report: ${it.message}")) }
                                        )
                                },
                                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not update report: ${it.message}")) }
                            )
                    },
                    onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not restore Dvar Torah: ${it.message}")) }
                )
        }
    }

    fun saveAdminNote(report: Report, adminNote: String) {
        viewModelScope.launch {
            reportRepository.updateAdminNote(report.id, adminNote).fold(
                onSuccess = { _effect.send(AdminUiEffect.ShowMessage("Admin note saved")) },
                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not save admin note: ${it.message}")) }
            )
        }
    }

    fun reopenReport(report: Report) {
        viewModelScope.launch {
            reportRepository.reopenReport(report.id).fold(
                onSuccess = { _effect.send(AdminUiEffect.ShowMessage("Report reopened")) },
                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Could not reopen report: ${it.message}")) }
            )
        }
    }
}
