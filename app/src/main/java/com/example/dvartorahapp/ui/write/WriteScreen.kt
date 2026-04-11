package com.example.dvartorahapp.ui.write

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dvartorahapp.data.model.OccasionCategory
import com.example.dvartorahapp.data.model.ParshaOccasion
import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.ui.components.LoadingOverlay

private val FieldShape = RoundedCornerShape(6.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    onNavigateBack: () -> Unit,
    currentUser: UserProfile,
    viewModel: WriteViewModel = hiltViewModel()
) {
    val isLoading         by viewModel.isLoading.collectAsState()
    val snackbarHostState  = remember { SnackbarHostState() }
    var dropdownExpanded  by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is WriteUiEffect.NavigateBack -> onNavigateBack()
                is WriteUiEffect.ShowError    -> snackbarHostState.showSnackbar(effect.message)
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
                            text  = if (viewModel.title.isBlank()) "Write a Dvar Torah" else "Edit Submission",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = viewModel.selectedOccasion?.displayNameEn ?: "Current parsha will load automatically",
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
                        onClick  = { viewModel.submit(currentUser.uid, currentUser.displayName) },
                        enabled  = !isLoading,
                        shape    = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(end = 8.dp).height(36.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Text("Publish", style = MaterialTheme.typography.labelLarge)
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Craft something worth saving for Shabbos.",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "The occasion field defaults to the current parsha. You can still change it before publishing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                WriteField(label = "Title") {
                    OutlinedTextField(
                        value         = viewModel.title,
                        onValueChange = { viewModel.title = it },
                        placeholder   = { Text("Enter a title", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine    = true,
                        shape         = FieldShape,
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = shadcnColors()
                    )
                }

                WriteField(label = "Parsha / Occasion") {
                    ExposedDropdownMenuBox(
                        expanded        = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value         = viewModel.selectedOccasion?.displayNameEn ?: "",
                            onValueChange = {},
                            readOnly      = true,
                            placeholder   = { Text("Select occasion", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            shape         = FieldShape,
                            modifier      = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            colors = shadcnColors()
                        )
                        ExposedDropdownMenu(
                            expanded        = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            Text(
                                text     = "Parsha",
                                style    = MaterialTheme.typography.labelSmall,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                            ParshaOccasion.entries
                                .filter { it.category == OccasionCategory.PARSHA }
                                .forEach { occasion ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(occasion.displayNameEn, style = MaterialTheme.typography.bodyMedium)
                                                Text(
                                                    occasion.displayNameHe,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectedOccasion = occasion
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                            Text(
                                text     = "Yom Tov",
                                style    = MaterialTheme.typography.labelSmall,
                                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                            ParshaOccasion.entries
                                .filter { it.category == OccasionCategory.YOM_TOV }
                                .forEach { occasion ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(occasion.displayNameEn, style = MaterialTheme.typography.bodyMedium)
                                                Text(
                                                    occasion.displayNameHe,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectedOccasion = occasion
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                        }
                    }
                }

                WriteField(label = "Author") {
                    OutlinedTextField(
                        value         = currentUser.displayName,
                        onValueChange = {},
                        readOnly      = true,
                        enabled       = false,
                        shape         = FieldShape,
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = shadcnColors()
                    )
                }

                WriteField(label = "Body") {
                    OutlinedTextField(
                        value         = viewModel.body,
                        onValueChange = { viewModel.body = it },
                        placeholder   = { Text("Write your Dvar Torah here…", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        minLines      = 10,
                        shape         = FieldShape,
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = shadcnColors()
                    )
                }

                WriteField(label = "Sources (optional)") {
                    OutlinedTextField(
                        value         = viewModel.sources,
                        onValueChange = { viewModel.sources = it },
                        placeholder   = { Text("e.g. Rashi on Bereishit 1:1", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        minLines      = 3,
                        shape         = FieldShape,
                        modifier      = Modifier.fillMaxWidth(),
                        colors        = shadcnColors()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun WriteField(label: String, field: @Composable () -> Unit) {
    Column {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        field()
    }
}

@Composable
private fun shadcnColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    disabledBorderColor  = MaterialTheme.colorScheme.outline,
    disabledTextColor    = MaterialTheme.colorScheme.onSurfaceVariant
)
