package com.example.dvartorahapp.ui.apply

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriterApplicationScreen(
    onNavigateBack: () -> Unit,
    currentUser: UserProfile,
    viewModel: WriterApplicationViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val currentApplication by viewModel.currentApplication.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var motivation by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser.uid) {
        viewModel.loadApplication(currentUser.uid)
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ApplyUiEffect.Success -> submitted = true
                is ApplyUiEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Apply to Write") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            LoadingOverlay(modifier = Modifier.padding(padding))
        } else if (currentApplication?.status == FirestoreConstants.ApplicationStatus.PENDING) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("Application Pending", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Your writer application is waiting for admin review. You will gain writer access once it is approved.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack) { Text("Back") }
                }
            }
        } else if (currentApplication?.status == FirestoreConstants.ApplicationStatus.APPROVED) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("Already Approved", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "This account has already been approved for writer access. If the Write tab is missing, sign out and back in.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack) { Text("Back") }
                }
            }
        } else if (submitted) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text("Application Submitted!", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "An admin will review your application. You'll receive writer access once approved.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onNavigateBack) { Text("Back to Feed") }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    if (currentApplication?.status == FirestoreConstants.ApplicationStatus.REJECTED) {
                        "Your previous application was rejected. You can update your motivation and submit again."
                    } else {
                        "We'd love to hear your Divrei Torah! Tell us a bit about yourself and why you'd like to contribute."
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                OutlinedTextField(
                    value = currentUser.displayName,
                    onValueChange = {},
                    label = { Text("Name") },
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = motivation,
                    onValueChange = { motivation = it },
                    label = { Text("Why would you like to write Divrei Torah?") },
                    minLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.submitApplication(currentUser, motivation) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Application")
                }
            }
        }
    }
}
