package com.quickdvartorah.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.quickdvartorah.app.data.model.DvarTorah
import com.quickdvartorah.app.data.model.UserProfile
import com.quickdvartorah.app.data.model.WriterApplication
import com.quickdvartorah.app.data.remote.FirestoreConstants
import com.quickdvartorah.app.data.remote.ParshaScheduleMode
import com.quickdvartorah.app.ui.components.DvarTorahCard
import com.quickdvartorah.app.ui.components.EditorialPanel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserProfile?,
    userDvareiTorah: List<DvarTorah>,
    userApplication: WriterApplication?,
    parshaScheduleMode: ParshaScheduleMode,
    showManageAdPrivacy: Boolean,
    isDeletingAccount: Boolean,
    onNavigateToApply: () -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToDvar: (String) -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToAccountDeletionPolicy: () -> Unit,
    onNavigateToContentPolicy: () -> Unit,
    onSignOut: () -> Unit,
    onParshaScheduleModeChange: (ParshaScheduleMode) -> Unit,
    onManageAdPrivacy: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    if (currentUser != null) {
                        TextButton(onClick = onSignOut) { Text("Sign out") }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.92f)
                )
            )
        }
    ) { padding ->
        if (currentUser != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    ),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProfileHeaderCard(
                        currentUser = currentUser,
                        userApplication = userApplication,
                        onNavigateToApply = onNavigateToApply,
                        onNavigateToWrite = onNavigateToWrite
                    )
                }

                item {
                    ProfileStatsCard(
                        role = currentUser.effectiveRole,
                        submissionCount = userDvareiTorah.size,
                        savedCountLabel = "Saved library"
                    )
                }

                item {
                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Parsha Schedule",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            androidx.compose.foundation.layout.FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ScheduleChip(
                                    label = "Device",
                                    selected = parshaScheduleMode == ParshaScheduleMode.DEVICE,
                                    onClick = { onParshaScheduleModeChange(ParshaScheduleMode.DEVICE) }
                                )
                                ScheduleChip(
                                    label = "Israel",
                                    selected = parshaScheduleMode == ParshaScheduleMode.ISRAEL,
                                    onClick = { onParshaScheduleModeChange(ParshaScheduleMode.ISRAEL) }
                                )
                                ScheduleChip(
                                    label = "Diaspora",
                                    selected = parshaScheduleMode == ParshaScheduleMode.DIASPORA,
                                    onClick = { onParshaScheduleModeChange(ParshaScheduleMode.DIASPORA) }
                                )
                            }
                        }
                    }
                }

                if (currentUser.isViewerOnly && userApplication != null) {
                    item {
                        val message = when (userApplication.status) {
                            FirestoreConstants.ApplicationStatus.PENDING -> "Your application is under review."
                            FirestoreConstants.ApplicationStatus.REJECTED -> "Your last application was not approved. You can submit a new one."
                            FirestoreConstants.ApplicationStatus.APPROVED -> "Your application was approved. Writer access should appear after refresh."
                            else -> null
                        }
                        if (message != null) {
                            EditorialPanel {
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(18.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                item {
                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Privacy and Account",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (showManageAdPrivacy) {
                                OutlinedButton(
                                    onClick = onManageAdPrivacy,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Manage ad privacy")
                                }
                            }
                            OutlinedButton(
                                onClick = onNavigateToPrivacyPolicy,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Privacy policy")
                            }
                            OutlinedButton(
                                onClick = onNavigateToAccountDeletionPolicy,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Account deletion policy")
                            }
                            OutlinedButton(
                                onClick = onNavigateToContentPolicy,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Content policy")
                            }
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isDeletingAccount
                            ) {
                                Text(if (isDeletingAccount) "Deleting account..." else "Delete account")
                            }
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "My Submissions",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (userDvareiTorah.isEmpty()) {
                    item {
                        EditorialPanel {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Your page is quiet",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                } else {
                    items(userDvareiTorah, key = { it.id }) { dvar ->
                        DvarTorahCard(
                            dvarTorah = dvar,
                            onCardClick = { onNavigateToDvar(dvar.id) }
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete account?",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "This removes your profile, applications, reports, likes, and authored Divrei Torah from the app.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteAccount()
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileHeaderCard(
    currentUser: UserProfile,
    userApplication: WriterApplication?,
    onNavigateToApply: () -> Unit,
    onNavigateToWrite: () -> Unit
) {
    EditorialPanel {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.large
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = currentUser.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = currentUser.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = when (currentUser.effectiveRole) {
                            FirestoreConstants.Roles.ADMIN -> "Administrator"
                            FirestoreConstants.Roles.WRITER -> "Writer"
                            else -> "Viewer"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            val showApplyButton = currentUser.isViewerOnly &&
                userApplication?.status != FirestoreConstants.ApplicationStatus.PENDING
            if (currentUser.hasWriterAccess || showApplyButton) {
                Button(
                    onClick = if (currentUser.hasWriterAccess) onNavigateToWrite else onNavigateToApply
                ) {
                    Icon(Icons.Outlined.EditNote, contentDescription = null)
                    Text(
                        text = if (currentUser.hasWriterAccess) "New submission" else "Apply for writer access",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStatsCard(
    role: String,
    submissionCount: Int,
    savedCountLabel: String
) {
    EditorialPanel {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBlock(
                modifier = Modifier.weight(1f),
                value = submissionCount.toString(),
                label = "Shared"
            )
            StatBlock(
                modifier = Modifier.weight(1f),
                value = when (role) {
                    FirestoreConstants.Roles.ADMIN -> "Admin"
                    FirestoreConstants.Roles.WRITER -> "Writer"
                    else -> "Viewer"
                },
                label = "Role"
            )
            StatBlock(
                modifier = Modifier.weight(1f),
                value = "Active",
                label = savedCountLabel
            )
        }
    }
}

@Composable
private fun StatBlock(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = MaterialTheme.shapes.medium
            )
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ScheduleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = MaterialTheme.shapes.large
    )
}
