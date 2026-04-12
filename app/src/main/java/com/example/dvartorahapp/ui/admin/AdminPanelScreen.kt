package com.example.dvartorahapp.ui.admin

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.HowToReg
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dvartorahapp.data.model.ExternalSubmission
import com.example.dvartorahapp.data.model.Report
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.model.WriterApplication
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.ui.components.EditorialPanel
import java.text.SimpleDateFormat
import java.util.Locale

private enum class ReviewedFilter {
    ALL,
    FLAGGED,
    REMOVED,
    PUBLISHED,
    DISMISSED
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    onOpenDvar: (String) -> Unit,
    currentUser: UserProfile,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val pendingApplications by viewModel.pendingApplications.collectAsStateWithLifecycle()
    val pendingReports by viewModel.pendingReports.collectAsStateWithLifecycle()
    val reviewedReports by viewModel.reviewedReports.collectAsStateWithLifecycle()
    val pendingExternalSubmissions by viewModel.pendingExternalSubmissions.collectAsStateWithLifecycle()
    val reviewedExternalSubmissions by viewModel.reviewedExternalSubmissions.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val uriHandler = LocalUriHandler.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var reviewedFilter by remember { mutableStateOf(ReviewedFilter.ALL) }
    val filteredReviewedReports = remember(reviewedReports, reviewedFilter) {
        reviewedReports.filter { report ->
            when (reviewedFilter) {
                ReviewedFilter.ALL -> true
                ReviewedFilter.FLAGGED -> report.dvarStatus == FirestoreConstants.DvarTorahStatus.FLAGGED
                ReviewedFilter.REMOVED -> report.dvarStatus == FirestoreConstants.DvarTorahStatus.REMOVED
                ReviewedFilter.PUBLISHED -> report.dvarStatus == FirestoreConstants.DvarTorahStatus.PUBLISHED
                ReviewedFilter.DISMISSED -> report.status == FirestoreConstants.ReportStatus.DISMISSED
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AdminUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                EditorialPanel(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Admin tools",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Review applications and reports.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Signed in as ${currentUser.displayName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Applications (${pendingApplications.size})") },
                        icon = { Icon(Icons.Outlined.HowToReg, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Open Reports (${pendingReports.size})") },
                        icon = { Icon(Icons.Outlined.Flag, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Desktop (${pendingExternalSubmissions.size})") },
                        icon = { Icon(Icons.Outlined.HowToReg, contentDescription = null) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Reviewed (${reviewedReports.size + reviewedExternalSubmissions.size})") },
                        icon = { Icon(Icons.Outlined.Flag, contentDescription = null) }
                    )
                }

                Crossfade(targetState = selectedTab, label = "admin_tab") { tab ->
                when (tab) {
                    0 -> {
                        if (pendingApplications.isEmpty()) {
                            AdminEmptyState(
                                title = "No pending applications",
                                description = "There are no applications to review."
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(pendingApplications, key = { it.id }) { application ->
                                    ApplicationModerationCard(
                                        application = application,
                                        onApprove = { viewModel.approveApplication(application, currentUser.uid) },
                                        onReject = { viewModel.rejectApplication(application, currentUser.uid) }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        if (pendingReports.isEmpty()) {
                            AdminEmptyState(
                                title = "No pending reports",
                                description = "There are no reports to review."
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(pendingReports, key = { it.id }) { report ->
                                    ReportModerationCard(
                                        report = report,
                                        isReviewed = false,
                                        onOpen = { onOpenDvar(report.dvarId) },
                                        onFlag = { note -> viewModel.flagContent(report, currentUser.uid, note) },
                                        onRemove = { note -> viewModel.removeContent(report, currentUser.uid, note) },
                                        onDismiss = { note -> viewModel.dismissReport(report, currentUser.uid, note) },
                                        onRestore = null
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        if (pendingExternalSubmissions.isEmpty()) {
                            AdminEmptyState(
                                title = "No desktop submissions",
                                description = "Pending computer submissions will appear here."
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(pendingExternalSubmissions, key = { it.id }) { submission ->
                                    ExternalSubmissionCard(
                                        submission = submission,
                                        isReviewed = false,
                                        onOpenDoc = {
                                            if (submission.documentUrl.isNotBlank()) {
                                                uriHandler.openUri(submission.documentUrl)
                                            }
                                        },
                                        onPublish = { note ->
                                            viewModel.publishExternalSubmission(submission, currentUser.uid, note)
                                        },
                                        onReject = { note ->
                                            viewModel.rejectExternalSubmission(submission, currentUser.uid, note)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        if (reviewedReports.isEmpty() && reviewedExternalSubmissions.isEmpty()) {
                            AdminEmptyState(
                                title = "No reviewed items",
                                description = "Reviewed reports and desktop submissions will appear here."
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        ReviewedFilterChip("All", reviewedFilter == ReviewedFilter.ALL) { reviewedFilter = ReviewedFilter.ALL }
                                        ReviewedFilterChip("Flagged", reviewedFilter == ReviewedFilter.FLAGGED) { reviewedFilter = ReviewedFilter.FLAGGED }
                                        ReviewedFilterChip("Removed", reviewedFilter == ReviewedFilter.REMOVED) { reviewedFilter = ReviewedFilter.REMOVED }
                                        ReviewedFilterChip("Published", reviewedFilter == ReviewedFilter.PUBLISHED) { reviewedFilter = ReviewedFilter.PUBLISHED }
                                        ReviewedFilterChip("Dismissed", reviewedFilter == ReviewedFilter.DISMISSED) { reviewedFilter = ReviewedFilter.DISMISSED }
                                    }
                                }
                                items(filteredReviewedReports, key = { it.id }) { report ->
                                    ReportModerationCard(
                                        report = report,
                                        isReviewed = true,
                                        onOpen = { onOpenDvar(report.dvarId) },
                                        onFlag = { },
                                        onRemove = { },
                                        onDismiss = { },
                                        onRestore = { note -> viewModel.restoreContent(report, currentUser.uid, note) },
                                        onSaveNote = { note -> viewModel.saveAdminNote(report, note) },
                                        onReopen = { viewModel.reopenReport(report) }
                                    )
                                }
                                items(reviewedExternalSubmissions, key = { "external_${it.id}" }) { submission ->
                                    ExternalSubmissionCard(
                                        submission = submission,
                                        isReviewed = true,
                                        onOpenDoc = {
                                            if (submission.documentUrl.isNotBlank()) {
                                                uriHandler.openUri(submission.documentUrl)
                                            }
                                        },
                                        onPublish = { },
                                        onReject = { },
                                        onSaveNote = { note ->
                                            viewModel.saveExternalSubmissionNote(submission, note)
                                        },
                                        onReopen = {
                                            viewModel.reopenExternalSubmission(submission)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                }
            }
        }
    }
}

@Composable
private fun ExternalSubmissionCard(
    submission: ExternalSubmission,
    isReviewed: Boolean,
    onOpenDoc: () -> Unit,
    onPublish: (String) -> Unit,
    onReject: (String) -> Unit,
    onSaveNote: ((String) -> Unit)? = null,
    onReopen: (() -> Unit)? = null
) {
    val submittedAt = remember(submission.submittedAt) { submission.submittedAt?.toDate()?.let(::formatAdminDate) }
    val reviewedAt = remember(submission.reviewedAt) { submission.reviewedAt?.toDate()?.let(::formatAdminDate) }
    EditorialPanel {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = submission.title.ifBlank { "Untitled desktop submission" },
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = submission.parshaOccasion?.displayNameEn ?: submission.occasion.ifBlank { "No parsha selected" },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = buildString {
                    append(submission.submitterName.ifBlank { "Unknown submitter" })
                    if (submission.submitterEmail.isNotBlank()) {
                        append(" • ")
                        append(submission.submitterEmail)
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = submission.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (submission.sources.isNotBlank()) {
                Text(
                    text = "Sources: ${submission.sources}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (submission.documentUrl.isNotBlank()) {
                Text(
                    text = submission.documentUrl,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text = "Status: ${submission.status.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            submittedAt?.let {
                Text(
                    text = "Submitted $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            reviewedAt?.let {
                Text(
                    text = "Reviewed $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (submission.publishedDvarId.isNotBlank()) {
                Text(
                    text = "Published Dvar ID: ${submission.publishedDvarId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            var noteText by remember(submission.id, submission.adminNote) { mutableStateOf(submission.adminNote) }
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Admin note") },
                modifier = Modifier.fillMaxWidth(),
                minLines = if (isReviewed) 2 else 3
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (submission.documentUrl.isNotBlank()) {
                    OutlinedButton(
                        onClick = onOpenDoc,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Open doc")
                    }
                }
                if (!isReviewed) {
                    Button(
                        onClick = { onPublish(noteText) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Publish")
                    }
                }
            }
            if (!isReviewed) {
                OutlinedButton(
                    onClick = { onReject(noteText) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reject")
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onSaveNote?.invoke(noteText) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save note")
                    }
                    OutlinedButton(
                        onClick = { onReopen?.invoke() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reopen")
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminEmptyState(title: String, description: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        EditorialPanel(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ApplicationModerationCard(
    application: WriterApplication,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    EditorialPanel {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = application.applicantName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = application.applicantEmail,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = application.motivation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Approve")
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Reject")
                }
            }
        }
    }
}

@Composable
private fun ReportModerationCard(
    report: Report,
    isReviewed: Boolean,
    onOpen: () -> Unit,
    onFlag: (String) -> Unit,
    onRemove: (String) -> Unit,
    onDismiss: (String) -> Unit,
    onRestore: ((String) -> Unit)?,
    onSaveNote: ((String) -> Unit)? = null,
    onReopen: (() -> Unit)? = null
) {
    val submittedAt = remember(report.submittedAt) { report.submittedAt?.toDate()?.let(::formatAdminDate) }
    val reviewedAt = remember(report.reviewedAt) { report.reviewedAt?.toDate()?.let(::formatAdminDate) }
    EditorialPanel {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = report.reason,
                style = MaterialTheme.typography.titleMedium
            )
            if (report.dvarTitle.isNotBlank()) {
                Text(
                    text = report.dvarTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = if (report.dvarAuthorName.isNotBlank()) {
                    "by ${report.dvarAuthorName}"
                } else {
                    "Dvar Torah ID: ${report.dvarId}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (report.reporterName.isNotBlank()) {
                Text(
                    text = buildString {
                        append("Reported by ")
                        append(report.reporterName)
                        if (report.reporterEmail.isNotBlank()) {
                            append(" • ")
                            append(report.reporterEmail)
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (report.dvarBodyPreview.isNotBlank()) {
                Text(
                    text = report.dvarBodyPreview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Dvar Torah status: ${report.dvarStatus.replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            submittedAt?.let {
                Text(
                    text = "Submitted $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (report.adminNote.isNotBlank() || isReviewed) {
                Text(
                    text = "Status: ${report.status.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            reviewedAt?.let {
                Text(
                    text = "Reviewed $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (report.reviewedBy.isNotBlank()) {
                Text(
                    text = "Reviewed by ${report.reviewedBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            var noteText by remember(report.id, report.adminNote) { mutableStateOf(report.adminNote) }
            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                label = { Text("Admin note") },
                modifier = Modifier.fillMaxWidth(),
                minLines = if (isReviewed) 2 else 3,
                readOnly = false
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Open")
                }
                if (!isReviewed) {
                    OutlinedButton(
                        onClick = { onFlag(noteText) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Flag")
                    }
                } else if (onRestore != null && report.status == "actioned") {
                    OutlinedButton(
                        onClick = { onRestore(noteText) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Restore")
                    }
                }
            }
            if (!isReviewed) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onRemove(noteText) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Remove")
                    }
                }
                TextButton(
                    onClick = { onDismiss(noteText) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dismiss report")
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { onSaveNote?.invoke(noteText) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save note")
                    }
                    OutlinedButton(
                        onClick = { onReopen?.invoke() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Reopen")
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewedFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) }
    )
}

private fun formatAdminDate(date: java.util.Date): String =
    SimpleDateFormat("MMM d, yyyy h:mm a", Locale.US).format(date)
