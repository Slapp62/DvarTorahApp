package com.example.dvartorahapp.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dvartorahapp.data.model.OccasionCategory
import com.example.dvartorahapp.data.model.ParshaOccasion
import com.example.dvartorahapp.ui.components.DvarTorahCard
import com.example.dvartorahapp.ui.components.ErrorMessage
import com.example.dvartorahapp.ui.components.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onDvarClick: (String) -> Unit,
    onLikeClick: ((String) -> Unit)? = null,
    likedDvarIds: Set<String> = emptySet(),
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    val currentParsha by viewModel.currentParsha.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ShabbosVorts",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = currentParsha?.occasion?.displayNameHe ?: "דברי תורה לשבת",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column {
                currentParsha?.let { info ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "This Week's Parsha",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = info.occasion.displayNameEn,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = info.occasion.displayNameHe,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AutoStories,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        ShadcnFilterChip(
                            selected = activeFilter == null,
                            label = "All",
                            onClick = { viewModel.setFilter(null) }
                        )
                    }
                    items(ParshaOccasion.entries.filter { it.category == OccasionCategory.PARSHA }) { occasion ->
                        ShadcnFilterChip(
                            selected = activeFilter == occasion,
                            label = occasion.displayNameEn,
                            onClick = { viewModel.setFilter(if (activeFilter == occasion) null else occasion) }
                        )
                    }
                    item { ShadcnDividerChip() }
                    items(ParshaOccasion.entries.filter { it.category == OccasionCategory.YOM_TOV }) { occasion ->
                        ShadcnFilterChip(
                            selected = activeFilter == occasion,
                            label = occasion.displayNameEn,
                            onClick = { viewModel.setFilter(if (activeFilter == occasion) null else occasion) }
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                when (val state = uiState) {
                    is FeedUiState.Loading -> LoadingOverlay()
                    is FeedUiState.Error -> ErrorMessage(state.message)
                    is FeedUiState.Success -> {
                        if (state.items.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "No Divrei Torah yet",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (activeFilter != null) {
                                            "Nothing has been posted yet for ${activeFilter!!.displayNameEn}"
                                        } else {
                                            "Check back soon"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.items, key = { it.id }) { dvar ->
                                    DvarTorahCard(
                                        dvarTorah = dvar,
                                        isLiked = dvar.id in likedDvarIds,
                                        onCardClick = { onDvarClick(dvar.id) },
                                        onLikeClick = onLikeClick?.let { { it(dvar.id) } }
                                    )
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
private fun ShadcnFilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    val shape = RoundedCornerShape(999.dp)
    FilterChip(
        selected = selected,
        onClick  = onClick,
        label    = { Text(label, style = MaterialTheme.typography.labelMedium) },
        shape    = shape,
        colors   = FilterChipDefaults.filterChipColors(
            selectedContainerColor  = MaterialTheme.colorScheme.primary,
            selectedLabelColor      = MaterialTheme.colorScheme.onPrimary,
            containerColor          = MaterialTheme.colorScheme.surface,
            labelColor              = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled               = true,
            selected              = selected,
            borderColor           = MaterialTheme.colorScheme.outline,
            selectedBorderColor   = MaterialTheme.colorScheme.primary,
            borderWidth           = 1.dp,
            selectedBorderWidth   = 1.dp
        )
    )
}

@Composable
private fun ShadcnDividerChip() {
    Box(
        modifier = Modifier
            .height(32.dp)
            .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "·",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
