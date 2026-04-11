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

    private val _effect = Channel<AdminUiEffect>()
    val effect = _effect.receiveAsFlow()

    fun approveApplication(application: WriterApplication, reviewerUid: String) {
        viewModelScope.launch {
            applicationRepository.approveApplication(application.id, application.applicantUid, reviewerUid).fold(
                onSuccess = { _effect.send(AdminUiEffect.ShowMessage("${application.applicantName} approved as Writer")) },
                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Failed to approve: ${it.message}")) }
            )
        }
    }

    fun rejectApplication(application: WriterApplication, reviewerUid: String) {
        viewModelScope.launch {
            applicationRepository.rejectApplication(application.id, reviewerUid).fold(
                onSuccess = { _effect.send(AdminUiEffect.ShowMessage("Application rejected")) },
                onFailure = { _effect.send(AdminUiEffect.ShowMessage("Failed to reject: ${it.message}")) }
            )
        }
    }

    fun flagContent(report: Report) {
        viewModelScope.launch {
            dvarTorahRepository.updateDvarTorahStatus(report.dvarId, FirestoreConstants.DvarTorahStatus.FLAGGED)
            reportRepository.updateReportStatus(report.id, FirestoreConstants.ReportStatus.ACTIONED)
            _effect.send(AdminUiEffect.ShowMessage("Content flagged"))
        }
    }

    fun removeContent(report: Report) {
        viewModelScope.launch {
            dvarTorahRepository.updateDvarTorahStatus(report.dvarId, FirestoreConstants.DvarTorahStatus.REMOVED)
            reportRepository.updateReportStatus(report.id, FirestoreConstants.ReportStatus.ACTIONED)
            _effect.send(AdminUiEffect.ShowMessage("Content removed"))
        }
    }

    fun dismissReport(report: Report) {
        viewModelScope.launch {
            reportRepository.updateReportStatus(report.id, FirestoreConstants.ReportStatus.DISMISSED)
            _effect.send(AdminUiEffect.ShowMessage("Report dismissed"))
        }
    }
}
