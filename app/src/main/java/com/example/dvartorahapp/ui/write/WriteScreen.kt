package com.example.dvartorahapp.ui.write

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dvartorahapp.data.model.OccasionCategory
import com.example.dvartorahapp.data.model.ParshaOccasion
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.ui.components.EditorialPanel
import com.example.dvartorahapp.ui.components.LoadingOverlay

private val FieldShape = RoundedCornerShape(6.dp)

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
                    Column {
                        Text(
                            text = if (viewModel.isEditing) "Edit Dvar Torah" else "Write a Dvar Torah",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = viewModel.selectedOccasion?.displayNameEn ?: "This week's parsha will load automatically",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.submit(currentUser.uid, currentUser.displayName) },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(38.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text(if (viewModel.isEditing) "Save" else "Publish", style = MaterialTheme.typography.labelLarge)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
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
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.42f),
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    EditorialPanel(
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (viewModel.isEditing) "Edit your Dvar Torah" else "Write your Dvar Torah",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Write clearly and include any sources you want readers to see.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            WriteField("Title") {
                                OutlinedTextField(
                                    value = viewModel.title,
                                    onValueChange = { viewModel.title = it },
                                    placeholder = { Text("Enter a title", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    singleLine = true,
                                    shape = FieldShape,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = writeFieldColors()
                                )
                            }

                            WriteField("Parsha or Yom Tov") {
                                ExposedDropdownMenuBox(
                                    expanded = dropdownExpanded,
                                    onExpandedChange = { dropdownExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = viewModel.selectedOccasion?.displayNameEn ?: "",
                                        onValueChange = {},
                                        readOnly = true,
                                        placeholder = { Text("Select a parsha or Yom Tov", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                        shape = FieldShape,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
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
                                    }
                                }
                            }

                            WriteField("Author") {
                                OutlinedTextField(
                                    value = currentUser.displayName,
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = false,
                                    shape = FieldShape,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = writeFieldColors()
                                )
                            }
                        }
                    }

                    EditorialPanel {
                        Column(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            WriteField("Body") {
                                OutlinedTextField(
                                    value = viewModel.body,
                                    onValueChange = { viewModel.body = it },
                                    placeholder = { Text("Write your Dvar Torah", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    minLines = 12,
                                    shape = FieldShape,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = writeFieldColors()
                                )
                            }

                            WriteField("Sources") {
                                OutlinedTextField(
                                    value = viewModel.sources,
                                    onValueChange = { viewModel.sources = it },
                                    placeholder = { Text("Example: Rashi on Bereishit 1:1", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    minLines = 3,
                                    shape = FieldShape,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = writeFieldColors()
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun WriteField(label: String, field: @Composable () -> Unit) {
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
                        occasion.displayNameHe,
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
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
    unfocusedContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.55f),
    disabledContainerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.4f)
)
