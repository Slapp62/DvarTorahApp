package com.example.dvartorahapp.ui.apply

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.ui.components.EditorialPanel
import com.example.dvartorahapp.ui.components.LoadingOverlay

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun WriterApplicationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToContentPolicy: () -> Unit = {},
    currentUser: UserProfile,
    viewModel: WriterApplicationViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val currentApplication by viewModel.currentApplication.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var motivation by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    var agreedToContentPolicy by remember { mutableStateOf(false) }

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Apply to write") },
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
        if (isLoading) {
            LoadingOverlay(
                modifier = Modifier.padding(padding),
                label = "Loading application"
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            ) {
                Crossfade(
                    targetState = when {
                        currentApplication?.status == FirestoreConstants.ApplicationStatus.PENDING -> "pending"
                        currentApplication?.status == FirestoreConstants.ApplicationStatus.APPROVED -> "approved"
                        submitted -> "submitted"
                        else -> "form"
                    },
                    label = "application_state"
                ) { applicationState ->
                when (applicationState) {
                    "pending" -> {
                        ApplicationStatePanel(
                            title = "Application pending",
                            description = "Your application is under review.",
                            actionLabel = "Back",
                            onAction = onNavigateBack
                        )
                    }

                    "approved" -> {
                        ApplicationStatePanel(
                            title = "Already approved",
                            description = "This account already has writer access.",
                            actionLabel = "Back",
                            onAction = onNavigateBack
                        )
                    }

                    "submitted" -> {
                        ApplicationStatePanel(
                            title = "Application submitted",
                            description = "Your application was submitted for review.",
                            actionLabel = "Back",
                            onAction = onNavigateBack
                        )
                    }

                    else -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .verticalScroll(rememberScrollState())
                                .imePadding(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            EditorialPanel(
                                modifier = Modifier.padding(top = 12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 22.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.EditNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "Apply to write",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (currentApplication?.status == FirestoreConstants.ApplicationStatus.REJECTED) {
                                            "Your last application was not approved. You can update it and submit again."
                                        } else {
                                            "Tell the admins why you want to write for the app."
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            EditorialPanel {
                                Column(
                                    modifier = Modifier
                                        .animateContentSize()
                                        .padding(horizontal = 18.dp, vertical = 18.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ApplicationField("Name") {
                                        OutlinedTextField(
                                            value = currentUser.displayName,
                                            onValueChange = {},
                                            readOnly = true,
                                            enabled = false,
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = applicationFieldColors()
                                        )
                                    }

                                    ApplicationField("Why do you want to write?") {
                                        OutlinedTextField(
                                            value = motivation,
                                            onValueChange = { motivation = it },
                                            placeholder = {
                                                Text(
                                                    "Tell us a little about what you want to write.",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            },
                                            minLines = 7,
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = applicationFieldColors()
                                        )
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        androidx.compose.foundation.layout.Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = agreedToContentPolicy,
                                                onCheckedChange = { agreedToContentPolicy = it }
                                            )
                                            Text(
                                                text = "I agree to follow the content policy.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        androidx.compose.material3.TextButton(
                                            onClick = onNavigateToContentPolicy
                                        ) {
                                            Text("Read content policy")
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.submitApplication(
                                                currentUser,
                                                motivation,
                                                agreedToContentPolicy
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                    ) {
                                        Text("Submit application", style = MaterialTheme.typography.labelLarge)
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
}

@Composable
private fun ApplicationStatePanel(
    title: String,
    description: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        EditorialPanel {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onAction) {
                    Text(actionLabel)
                }
            }
        }
    }
}

@Composable
private fun ApplicationField(label: String, field: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        field()
    }
}

@Composable
private fun applicationFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
    unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
    disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
)
