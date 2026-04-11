package com.example.dvartorahapp.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.ui.components.ErrorMessage
import com.example.dvartorahapp.ui.components.LoadingOverlay
import com.example.dvartorahapp.ui.components.RtlAwareText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DvarTorahDetailScreen(
    onNavigateBack: () -> Unit,
    currentUser: UserProfile?,
    onRequireAuth: () -> Unit,
    viewModel: DvarTorahDetailViewModel = hiltViewModel()
) {
    val uiState           by viewModel.uiState.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }
    var showReportSheet   by remember { mutableStateOf(false) }
    var selectedReason    by remember { mutableStateOf("") }

    val isLiked by remember(currentUser?.uid) {
        if (currentUser != null) viewModel.getUserLikedStatus(currentUser.uid)
        else kotlinx.coroutines.flow.flowOf(false)
    }.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (currentUser != null) {
                        IconButton(onClick = { showReportSheet = true }) {
                            Icon(
                                Icons.Outlined.Flag,
                                contentDescription = "Report",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> LoadingOverlay(modifier = Modifier.padding(padding))
            is DetailUiState.Error   -> ErrorMessage(state.message, modifier = Modifier.padding(padding))
            is DetailUiState.Success -> {
                val dvar = state.dvarTorah
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // Occasion badge
                    dvar.parshaOccasion?.let { occasion ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = occasion.displayNameEn.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Text(
                                text = occasion.displayNameHe,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Title
                    Text(
                        text = dvar.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "By ${dvar.authorName}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Body
                    RtlAwareText(
                        text  = dvar.body,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    // Sources
                    if (dvar.sources.isNotBlank()) {
                        Spacer(modifier = Modifier.height(28.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sources",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = dvar.sources,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Like row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                if (currentUser != null) viewModel.toggleLike(currentUser.uid)
                                else onRequireAuth()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isLiked) MaterialTheme.colorScheme.secondary
                                       else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (dvar.likeCount > 0) {
                            Text(
                                text = "${dvar.likeCount}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Report bottom sheet
    if (showReportSheet) {
        val reasons = listOf("Inappropriate content", "Spam", "Factual error", "Other")
        ModalBottomSheet(
            onDismissRequest = { showReportSheet = false },
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text("Report", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Select a reason for reporting this Dvar Torah",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick  = { selectedReason = reason }
                        )
                        Text(
                            text  = reason,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        if (selectedReason.isNotBlank() && currentUser != null) {
                            viewModel.submitReport(currentUser.uid, selectedReason)
                            showReportSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape    = RoundedCornerShape(6.dp),
                    enabled  = selectedReason.isNotBlank()
                ) {
                    Text("Submit report", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
