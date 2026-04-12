package com.example.dvartorahapp.ui.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.ui.components.EditorialPanel
import com.example.dvartorahapp.ui.components.ErrorMessage
import com.example.dvartorahapp.ui.components.LoadingOverlay
import com.example.dvartorahapp.ui.components.RtlAwareText

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DvarTorahDetailScreen(
    onNavigateBack: () -> Unit,
    currentUser: UserProfile?,
    onRequireAuth: () -> Unit,
    viewModel: DvarTorahDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showReportSheet by remember { mutableStateOf(false) }
    var selectedReason by remember { mutableStateOf("") }

    val isLiked by remember(currentUser?.uid) {
        if (currentUser != null) viewModel.getUserLikedStatus(currentUser.uid)
        else kotlinx.coroutines.flow.flowOf(false)
    }.collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is DetailUiEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
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
                            Icon(Icons.Outlined.Flag, contentDescription = "Report")
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
            is DetailUiState.Loading -> LoadingOverlay(
                modifier = Modifier.padding(padding),
                label = "Loading Dvar Torah"
            )
            is DetailUiState.Error -> ErrorMessage(state.message, modifier = Modifier.padding(padding))
            is DetailUiState.Success -> {
                val dvar = state.dvarTorah
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EditorialPanel {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateContentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.surface,
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.32f)
                                        )
                                    )
                                )
                                .padding(22.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            dvar.parshaOccasion?.let { occasion ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${occasion.displayNameEn} • ${occasion.displayNameHe}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Text(
                                text = dvar.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "by ${dvar.authorName}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.BookmarkBorder,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Dvar Torah",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    EditorialPanel {
                        Column(
                            modifier = Modifier
                                .animateContentSize()
                                .padding(horizontal = 20.dp, vertical = 22.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            RtlAwareText(
                                text = dvar.body,
                                style = MaterialTheme.typography.bodyLarge
                            )

                            if (dvar.sources.isNotBlank()) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Sources",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = dvar.sources,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    EditorialPanel {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (currentUser != null) viewModel.toggleLike(currentUser.uid)
                                        else onRequireAuth()
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = null,
                                        tint = if (isLiked) MaterialTheme.colorScheme.secondary
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = if (dvar.likeCount > 0) "${dvar.likeCount} likes" else "Be the first to like this Dvar Torah",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (currentUser != null) {
                                TextButton(onClick = { showReportSheet = true }) {
                                    Text("Report")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showReportSheet) {
        val reasons = listOf("Inappropriate content", "Spam", "Incorrect information", "Other")
        ModalBottomSheet(
            onDismissRequest = {
                showReportSheet = false
                selectedReason = ""
            },
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Report this Dvar Torah", style = MaterialTheme.typography.titleMedium)
                Text(
                    "Choose a reason for this report.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                reasons.forEach { reason ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedReason == reason,
                            onClick = { selectedReason = reason }
                        )
                        Text(
                            text = reason,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Button(
                    onClick = {
                        if (selectedReason.isNotBlank() && currentUser != null) {
                            viewModel.submitReport(currentUser, selectedReason)
                            showReportSheet = false
                            selectedReason = ""
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    enabled = selectedReason.isNotBlank()
                ) {
                    Text("Submit report")
                }
            }
        }
    }
}
