package com.example.dvartorahapp.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dvartorahapp.data.model.DvarTorah
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.model.WriterApplication
import com.example.dvartorahapp.data.remote.ParshaScheduleMode
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.ui.components.DvarTorahCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUser: UserProfile?,
    userDvareiTorah: List<DvarTorah>,
    userApplication: WriterApplication?,
    parshaScheduleMode: ParshaScheduleMode,
    onNavigateToLogin: () -> Unit,
    onNavigateToApply: () -> Unit,
    onNavigateToDvar: (String) -> Unit,
    onSignOut: () -> Unit,
    onParshaScheduleModeChange: (ParshaScheduleMode) -> Unit
) {
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
                modifier = Modifier.fillMaxSize().padding(padding),
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
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(currentUser.displayName, style = MaterialTheme.typography.titleLarge)
                            Text(currentUser.email, style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(modifier = Modifier.height(8.dp))
                            val roleBadgeColor = when (currentUser.effectiveRole) {
                                FirestoreConstants.Roles.ADMIN -> MaterialTheme.colorScheme.error
                                FirestoreConstants.Roles.WRITER -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.outline
                            }
                            AssistChip(
                                onClick = {},
                                label = { Text(currentUser.effectiveRole.replaceFirstChar { it.uppercase() }) },
                                colors = AssistChipDefaults.assistChipColors(labelColor = roleBadgeColor)
                            )
                        }
                    }
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Parsha Schedule", style = MaterialTheme.typography.titleSmall)
                            Text(
                                text = "Choose how the app determines the weekly parsha for defaults and feed filtering.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
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

                if (currentUser.isViewer && userApplication?.status != FirestoreConstants.ApplicationStatus.PENDING) {
                    item {
                        OutlinedButton(
                            onClick = onNavigateToApply,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Apply to Write Divrei Torah")
                        }
                    }
                }

                if (currentUser.isViewer && userApplication != null) {
                    item {
                        val message = when (userApplication.status) {
                            FirestoreConstants.ApplicationStatus.PENDING ->
                                "Your writer application is pending admin review."
                            FirestoreConstants.ApplicationStatus.REJECTED ->
                                "Your previous application was rejected. You can submit a new one."
                            FirestoreConstants.ApplicationStatus.APPROVED ->
                                "Your application was approved. Writer access should appear after your profile refreshes."
                            else -> null
                        }
                        if (message != null) {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = message,
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                if (userDvareiTorah.isNotEmpty()) {
                    item {
                        Text(
                            "My Divrei Torah",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp)
                        )
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
}

@Composable
private fun ScheduleChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(999.dp)
    )
}
