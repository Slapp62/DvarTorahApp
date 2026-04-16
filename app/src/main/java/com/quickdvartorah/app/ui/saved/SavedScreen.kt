package com.quickdvartorah.app.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickdvartorah.app.data.model.DvarTorah
import com.quickdvartorah.app.ui.components.EditorialPanel
import com.quickdvartorah.app.ui.components.ErrorMessage
import com.quickdvartorah.app.ui.components.LoadingOverlay

@Composable
fun SavedScreen(
    isAuthenticated: Boolean,
    onDvarClick: (String) -> Unit,
    onRequireAuth: () -> Unit,
    viewModel: SavedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.filter.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        when (val state = uiState) {
            SavedUiState.Loading -> LoadingOverlay(label = "Loading saved insights")
            is SavedUiState.Error -> ErrorMessage(state.message)
            SavedUiState.SignedOut -> {
                if (isAuthenticated) {
                    LoadingOverlay(label = "Refreshing saved insights")
                } else {
                    SavedSignedOut(onRequireAuth)
                }
            }
            is SavedUiState.Empty -> SavedEmpty(state.filter, onRequireAuth)
            is SavedUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Saved Insights",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SavedFilterChip("All Items", selectedFilter == SavedFilter.All) { viewModel.setFilter(SavedFilter.All) }
                            SavedFilterChip("Parsha", selectedFilter == SavedFilter.Parsha) { viewModel.setFilter(SavedFilter.Parsha) }
                            SavedFilterChip("Holidays", selectedFilter == SavedFilter.YomTov) { viewModel.setFilter(SavedFilter.YomTov) }
                            SavedFilterChip("Occasions", selectedFilter == SavedFilter.Occasion) { viewModel.setFilter(SavedFilter.Occasion) }
                        }
                    }
                    item {
                        SavedFeatureCard(state.items.first(), onClick = { onDvarClick(state.items.first().id) })
                    }
                    items(state.items.drop(1), key = { it.id }) { dvar ->
                        SavedListCard(dvar = dvar, onClick = { onDvarClick(dvar.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SavedSignedOut(onRequireAuth: () -> Unit) {
    EditorialPanel(
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = onRequireAuth)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.BookmarkBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Your library is quiet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Sign in",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun SavedEmpty(filter: SavedFilter, onRequireAuth: () -> Unit) {
    val message = when (filter) {
        SavedFilter.All -> "Save a few divrei Torah to start building your library."
        SavedFilter.Parsha -> "No saved parsha pieces yet."
        SavedFilter.YomTov -> "No saved Yom Tov pieces yet."
        SavedFilter.Occasion -> "No saved special-occasion pieces yet."
    }
    EditorialPanel(
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = onRequireAuth)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.LibraryBooks,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "Nothing saved here",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SavedFeatureCard(dvar: DvarTorah, onClick: () -> Unit) {
    EditorialPanel(modifier = Modifier.clickable(onClick = onClick)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = dvar.parshaOccasion?.displayNameEn ?: "Saved",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            Text(
                text = dvar.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = dvar.body.take(180).trim().let { if (dvar.body.length > 180) "$it..." else it },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${dvar.authorName} • ${estimatedReadTimeLabel(dvar.body)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SavedListCard(dvar: DvarTorah, onClick: () -> Unit) {
    EditorialPanel(modifier = Modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = dvar.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = dvar.parshaOccasion?.displayNameEn ?: dvar.authorName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = dvar.body.take(100).trim().let { if (dvar.body.length > 100) "$it..." else it },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SavedFilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = false,
            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.12f),
            borderWidth = 0.dp
        )
    )
}

private fun estimatedReadTimeLabel(body: String): String {
    val words = body.trim().split("\\s+".toRegex()).count { it.isNotBlank() }
    val minutes = maxOf(1, kotlin.math.ceil(words / 180.0).toInt())
    return "$minutes min read"
}
