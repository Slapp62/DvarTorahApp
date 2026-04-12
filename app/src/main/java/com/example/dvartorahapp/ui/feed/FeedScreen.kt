package com.example.dvartorahapp.ui.feed

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dvartorahapp.data.model.OccasionCategory
import com.example.dvartorahapp.data.model.ParshaOccasion
import com.example.dvartorahapp.ui.components.DvarTorahCard
import com.example.dvartorahapp.ui.components.EditorialPanel
import com.example.dvartorahapp.ui.components.ErrorMessage
import com.example.dvartorahapp.ui.components.LoadingOverlay

@Composable
fun FeedScreen(
    onDvarClick: (String) -> Unit,
    onLikeClick: ((String) -> Unit)? = null,
    likedDvarIds: Set<String> = emptySet(),
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val currentParsha by viewModel.currentParsha.collectAsStateWithLifecycle()
    val previousParsha by viewModel.previousParsha.collectAsStateWithLifecycle()
    val nextParsha by viewModel.nextParsha.collectAsStateWithLifecycle()
    var showParshaMenu by remember { mutableStateOf(false) }
    val parshaOptions = remember {
        ParshaOccasion.entries.filter { it.category == OccasionCategory.PARSHA }
    }
    val selectorLabel = when (val filter = selectedFilter) {
        FeedParshaFilter.All -> "All parshas"
        FeedParshaFilter.Previous -> previousParsha?.occasion?.displayNameEn ?: "Previous Parsha"
        FeedParshaFilter.Current -> currentParsha?.occasion?.displayNameEn ?: "This parsha"
        FeedParshaFilter.Next -> nextParsha?.occasion?.displayNameEn ?: "Next Parsha"
        is FeedParshaFilter.Specific -> filter.occasion.displayNameEn
    }

    Box(
        modifier = Modifier
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
        Column(modifier = Modifier.fillMaxSize()) {
            EditorialPanel(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surface,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.38f)
                                )
                            )
                        )
                        .padding(22.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "ShabbosVorts",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Find a Dvar Torah for Shabbos",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = currentParsha?.let {
                                    "This week: ${it.occasion.displayNameEn} • ${it.occasion.displayNameHe}"
                                } ?: "browse the latest divrei torah",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(54.dp)
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

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .clickable { showParshaMenu = true }
                                .padding(end = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = selectorLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Box(modifier = Modifier.fillMaxWidth()) {
                            DropdownMenu(
                                expanded = showParshaMenu,
                                onDismissRequest = { showParshaMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Previous Parsha") },
                                    onClick = {
                                        viewModel.setFilter(FeedParshaFilter.Previous)
                                        showParshaMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("This parsha") },
                                    onClick = {
                                        viewModel.setFilter(FeedParshaFilter.Current)
                                        showParshaMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("All parshas") },
                                    onClick = {
                                        viewModel.setFilter(FeedParshaFilter.All)
                                        showParshaMenu = false
                                    }
                                )
                                parshaOptions.forEach { parsha ->
                                    DropdownMenuItem(
                                        text = { Text(parsha.displayNameEn) },
                                        onClick = {
                                            viewModel.setFilter(FeedParshaFilter.Specific(parsha))
                                            showParshaMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FeedFilterChip(
                                    selected = selectedFilter == FeedParshaFilter.All,
                                    label = "All",
                                    onClick = { viewModel.setFilter(FeedParshaFilter.All) }
                                )
                            }
                            item {
                                FeedFilterChip(
                                    selected = selectedFilter == FeedParshaFilter.Previous,
                                    label = "Previous Parsha",
                                    onClick = { viewModel.setFilter(FeedParshaFilter.Previous) }
                                )
                            }
                            item {
                                FeedFilterChip(
                                    selected = selectedFilter == FeedParshaFilter.Current,
                                    label = "This Parsha",
                                    onClick = { viewModel.setFilter(FeedParshaFilter.Current) }
                                )
                            }
                            item {
                                FeedFilterChip(
                                    selected = selectedFilter == FeedParshaFilter.Next,
                                    label = "Next Parsha",
                                    onClick = { viewModel.setFilter(FeedParshaFilter.Next) }
                                )
                            }
                            if (selectedFilter is FeedParshaFilter.Specific) {
                                item {
                                    FeedFilterChip(
                                        selected = true,
                                        label = selectorLabel,
                                        onClick = { showParshaMenu = true }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Crossfade(
                targetState = uiState,
                label = "feed_state"
            ) { state ->
                when (state) {
                    is FeedUiState.Loading -> LoadingOverlay(label = "loading this week's divrei torah")
                    is FeedUiState.Error -> ErrorMessage(state.message)
                    is FeedUiState.Success -> {
                    val activeOccasion = state.activeFilter
                    if (state.items.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No Divrei Torah yet",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (activeOccasion != null) {
                                        "Nothing has been posted yet for ${activeOccasion?.displayNameEn ?: "this parsha"}."
                                    } else {
                                        "No published Divrei Torah yet."
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.padding(bottom = 6.dp)
                                ) {
                                    Text(
                                        text = activeOccasion?.displayNameEn ?: "Latest Divrei Torah",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (activeOccasion != null) {
                                            "Divrei Torah for ${activeOccasion.displayNameEn}."
                                        } else {
                                            "Browse the latest Divrei Torah."
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
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
private fun FeedFilterChip(selected: Boolean, label: String, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        shape = RoundedCornerShape(999.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = MaterialTheme.colorScheme.secondary,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp
        )
    )
}
