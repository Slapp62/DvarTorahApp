package com.example.dvartorahapp.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dvartorahapp.data.model.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    currentUser: UserProfile,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val pendingApplications by viewModel.pendingApplications.collectAsState()
    val pendingReports by viewModel.pendingReports.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AdminUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Applications (${pendingApplications.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Reports (${pendingReports.size})") }
                )
            }

            when (selectedTab) {
                0 -> {
                    if (pendingApplications.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No pending applications", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pendingApplications, key = { it.id }) { application ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(application.applicantName, style = MaterialTheme.typography.titleMedium)
                                        Text(application.applicantEmail, style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(application.motivation, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = { viewModel.approveApplication(application, currentUser.uid) },
                                                modifier = Modifier.weight(1f)
                                            ) { Text("Approve") }
                                            OutlinedButton(
                                                onClick = { viewModel.rejectApplication(application, currentUser.uid) },
                                                modifier = Modifier.weight(1f)
                                            ) { Text("Reject") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    if (pendingReports.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No pending reports", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(pendingReports, key = { it.id }) { report ->
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("Reason: ${report.reason}", style = MaterialTheme.typography.titleMedium)
                                        Text("Dvar ID: ${report.dvarId}", style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            OutlinedButton(
                                                onClick = { viewModel.flagContent(report) },
                                                modifier = Modifier.weight(1f)
                                            ) { Text("Flag") }
                                            Button(
                                                onClick = { viewModel.removeContent(report) },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                            ) { Text("Remove") }
                                        }
                                        TextButton(
                                            onClick = { viewModel.dismissReport(report) },
                                            modifier = Modifier.fillMaxWidth()
                                        ) { Text("Dismiss Report") }
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
