package com.example.dvartorahapp.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dvartorahapp.data.model.DvarTorah
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.model.WriterApplication
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.data.remote.ParshaScheduleMode
import com.example.dvartorahapp.ui.components.DvarTorahCard
import com.example.dvartorahapp.ui.components.EditorialPanel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserProfile?,
    userDvareiTorah: List<DvarTorah>,
    userApplication: WriterApplication?,
    parshaScheduleMode: ParshaScheduleMode,
    showManageAdPrivacy: Boolean,
    isDeletingAccount: Boolean,
    onNavigateToLogin: () -> Unit,
    onNavigateToApply: () -> Unit,
    onNavigateToDvar: (String) -> Unit,
    onSignOut: () -> Unit,
    onParshaScheduleModeChange: (ParshaScheduleMode) -> Unit,
    onManageAdPrivacy: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    if (currentUser != null) {
                        TextButton(onClick = onSignOut) { Text("Sign Out") }
                    }
                }
            )
        }
    ) { padding ->
        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Sign in to view your profile", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onNavigateToLogin) { Text("Sign In") }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Your account",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(currentUser.displayName, style = MaterialTheme.typography.titleLarge)
                            Text(
                                currentUser.email,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            val roleBadgeColor = when (currentUser.effectiveRole) {
                                FirestoreConstants.Roles.ADMIN -> MaterialTheme.colorScheme.error
                                FirestoreConstants.Roles.WRITER -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.outline
                            }
                            AssistChip(
                                onClick = {},
                                label = { Text(currentUser.effectiveRole.replaceFirstChar { it.uppercase() }) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = roleBadgeColor.copy(alpha = 0.14f),
                                    labelColor = roleBadgeColor
                                )
                            )
                        }
                    }
                }

                item {
                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Parsha schedule", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Choose which parsha schedule the app uses.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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

                item {
                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Privacy and account", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Use these controls for ad consent and account deletion.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !isDeletingAccount
                            ) {
                                Text(if (isDeletingAccount) "Deleting account..." else "Delete account")
                            }
                        }
                    }
                }

                if (currentUser.isViewer && userApplication?.status != FirestoreConstants.ApplicationStatus.PENDING) {
                    item {
                        OutlinedButton(
                            onClick = onNavigateToApply,
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text("Apply to write")
                        }
                    }
                }

                if (currentUser.isViewer && userApplication != null) {
                    item {
                        val message = when (userApplication.status) {
                            FirestoreConstants.ApplicationStatus.PENDING ->
                                "Your application is under review."
                            FirestoreConstants.ApplicationStatus.REJECTED ->
                                "Your last application was not approved. You can submit a new one."
                            FirestoreConstants.ApplicationStatus.APPROVED ->
                                "Your application was approved. Writer access should appear after refresh."
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

                if (userDvareiTorah.isNotEmpty()) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("My Divrei Torah", style = MaterialTheme.typography.titleMedium)
                            Text(
                                text = "Divrei Torah you have published.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
            title = { Text("Delete account?") },
            text = { Text("This removes your profile, applications, reports, likes, and authored Divrei Torah from the app.") },
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
private fun ScheduleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = MaterialTheme.shapes.large
    )
}
