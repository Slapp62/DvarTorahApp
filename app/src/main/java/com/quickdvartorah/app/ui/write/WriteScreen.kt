package com.quickdvartorah.app.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickdvartorah.app.data.model.OccasionCategory
import com.quickdvartorah.app.data.model.ParshaOccasion
import com.quickdvartorah.app.data.model.UserProfile
import com.quickdvartorah.app.data.validation.SubmissionValidation
import com.quickdvartorah.app.ui.components.EditorialPanel
import com.quickdvartorah.app.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    onNavigateBack: () -> Unit,
    currentUser: UserProfile,
    viewModel: WriteViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var showGuidelines by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WriteUiEffect.NavigateBack -> onNavigateBack()
                is WriteUiEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (viewModel.isEditing) "Edit Dvar Torah" else "Write Dvar Torah",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { showGuidelines = true },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Guidelines")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.92f)
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.96f),
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Button(
                        onClick = { viewModel.submit(currentUser.uid, currentUser.displayName) },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(999.dp),
                        contentPadding = PaddingValues(horizontal = 22.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                if (viewModel.isEditing) "Save Dvar Torah" else "Submit Dvar Torah",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            LoadingOverlay(modifier = Modifier.padding(padding))
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.34f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            WriteField("Title") {
                                OutlinedTextField(
                                    value = viewModel.title,
                                    onValueChange = { viewModel.title = it },
                                    placeholder = { Text("A meaningful heading...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = writeFieldColors()
                                )
                            }

                            WriteField("Occasion") {
                                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OccasionQuickChips(
                                        selectedOccasion = viewModel.selectedOccasion,
                                        onSelect = { viewModel.selectedOccasion = it },
                                        onOpenMore = { dropdownExpanded = true }
                                    )

                                    ExposedDropdownMenuBox(
                                        expanded = dropdownExpanded,
                                        onExpandedChange = { dropdownExpanded = it }
                                    ) {
                                        OutlinedTextField(
                                            value = viewModel.selectedOccasion?.displayNameEn ?: "",
                                            onValueChange = {},
                                            readOnly = true,
                                            placeholder = { Text("Select a parsha, Yom Tov, or special occasion", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                            shape = RoundedCornerShape(18.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            colors = writeFieldColors()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = dropdownExpanded,
                                            onDismissRequest = { dropdownExpanded = false }
                                        ) {
                                            OccasionDropdownSection(
                                                title = "Parsha",
                                                occasions = ParshaOccasion.entries.filter { it.category == OccasionCategory.PARSHA },
                                                onSelect = {
                                                    viewModel.selectedOccasion = it
                                                    dropdownExpanded = false
                                                }
                                            )
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                            OccasionDropdownSection(
                                                title = "Yom Tov",
                                                occasions = ParshaOccasion.entries.filter { it.category == OccasionCategory.YOM_TOV },
                                                onSelect = {
                                                    viewModel.selectedOccasion = it
                                                    dropdownExpanded = false
                                                }
                                            )
                                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                                            OccasionDropdownSection(
                                                title = "Special Occasion",
                                                occasions = ParshaOccasion.entries.filter { it.category == OccasionCategory.SPECIAL_OCCASION },
                                                onSelect = {
                                                    viewModel.selectedOccasion = it
                                                    dropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            WriteField("Author") {
                                OutlinedTextField(
                                    value = currentUser.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    shape = RoundedCornerShape(18.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = writeFieldColors()
                                )
                            }
                        }
                    }

                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            WriteField("Body") {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = viewModel.body,
                                        onValueChange = {
                                            if (it.length <= SubmissionValidation.BODY_MAX_LENGTH) {
                                                viewModel.body = it
                                            }
                                        },
                                        placeholder = { Text("Start your commentary here...", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                        minLines = 12,
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = writeFieldColors()
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${viewModel.body.trim().length}/${SubmissionValidation.BODY_MAX_LENGTH}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            WriteField("Sources") {
                                OutlinedTextField(
                                    value = viewModel.sources,
                                    onValueChange = { viewModel.sources = it },
                                    placeholder = { Text("Example: Rashi on Bereishit 1:1", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    minLines = 4,
                                    shape = RoundedCornerShape(20.dp),
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = writeFieldColors()
                                )
                            }
                        }
                    }

                    Box(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    if (showGuidelines) {
        WriterGuidelinesDialog(onDismiss = { showGuidelines = false })
    }
}

@Composable
private fun OccasionQuickChips(
    selectedOccasion: ParshaOccasion?,
    onSelect: (ParshaOccasion) -> Unit,
    onOpenMore: () -> Unit
) {
    val quickOptions = listOf(
        ParshaOccasion.BEREISHIT,
        ParshaOccasion.ROSH_HASHANA,
        ParshaOccasion.WEDDING
    )

    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        maxItemsInEachRow = 4
    ) {
        quickOptions.forEach { occasion ->
            FilterChip(
                selected = selectedOccasion == occasion,
                onClick = { onSelect(occasion) },
                label = { Text(occasion.displayNameEn) }
            )
        }
        AssistChip(
            onClick = onOpenMore,
            label = { Text("More") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.MoreHoriz,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        )
    }
}

@Composable
private fun WriteField(label: String, field: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        field()
    }
}

@Composable
private fun OccasionDropdownSection(
    title: String,
    occasions: List<ParshaOccasion>,
    onSelect: (ParshaOccasion) -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
    )
    occasions.forEach { occasion ->
        DropdownMenuItem(
            text = {
                Column {
                    Text(occasion.displayNameEn, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = occasion.displayNameHe,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            onClick = { onSelect(occasion) }
        )
    }
}

@Composable
private fun writeFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f),
    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f),
    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
)
