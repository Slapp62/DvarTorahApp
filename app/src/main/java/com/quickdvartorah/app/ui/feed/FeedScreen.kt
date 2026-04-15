package com.quickdvartorah.app.ui.feed

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.quickdvartorah.app.data.model.DvarTorah
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quickdvartorah.app.ui.components.DvarTorahCard
import com.quickdvartorah.app.ui.components.EditorialPanel
import com.quickdvartorah.app.ui.components.ErrorMessage
import com.quickdvartorah.app.ui.components.LoadingOverlay

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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.26f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(20.dp),
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
                            text = "Quick Dvar Torah",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 32.sp,
                                lineHeight = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.7).sp
                            ),
                            color = MaterialTheme.colorScheme.primary
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
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FeedFilterChip(
                                selected = selectedFilter == FeedParshaFilter.All,
                                label = "Most Recent",
                                onClick = { viewModel.setFilter(FeedParshaFilter.All) }
                            )
                        }
                        item {
                            FeedFilterChip(
                                selected = selectedFilter == FeedParshaFilter.Current,
                                label = "This Week",
                                onClick = { viewModel.setFilter(FeedParshaFilter.Current) }
                            )
                        }
                        item {
                            FeedFilterChip(
                                selected = selectedFilter == FeedParshaFilter.Next,
                                label = "Next",
                                onClick = { viewModel.setFilter(FeedParshaFilter.Next) }
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f)) {
                Crossfade(
                    targetState = uiState,
                    label = "feed_state"
                ) { state ->
                    when (state) {
                        is FeedUiState.Loading -> LoadingOverlay(label = "loading this week's divrei torah")
                        is FeedUiState.Error -> ErrorMessage(state.message)
                        is FeedUiState.Success -> {
                        val activeOccasion = state.activeOccasion
                        val activeTitle = when (state.activeFilter) {
                            FeedParshaFilter.All -> "Latest Divrei Torah"
                            FeedParshaFilter.Current -> activeOccasion?.displayNameEn ?: "This Parsha"
                            FeedParshaFilter.Previous -> activeOccasion?.displayNameEn ?: "Previous Parsha"
                            FeedParshaFilter.Next -> activeOccasion?.displayNameEn ?: "Next Parsha"
                            else -> activeOccasion?.displayNameEn ?: "Latest Divrei Torah"
                        }
                        val featured = state.items.firstOrNull()
                        val remaining = if (state.items.size > 1) state.items.drop(1) else emptyList()

                        if (state.items.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
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
                                        text = "Nothing has been posted yet for $activeTitle.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                item {
                                    MostRecentCard(
                                        title = activeTitle,
                                        currentParshaLabel = currentParsha?.occasion?.displayNameEn,
                                        mostRecent = featured,
                                        onClick = featured?.let { { onDvarClick(it.id) } }
                                    )
                                }

                                item {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Text(
                                                text = "More Recent",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            text = "${state.items.size} available",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (remaining.isNotEmpty()) {
                                    item {
                                        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                                            val stacked = maxWidth > 720.dp
                                            if (stacked) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                                    remaining.take(2).forEach { dvar ->
                                                        SecondaryInsightCard(
                                                            dvar = dvar,
                                                            modifier = Modifier.weight(1f),
                                                            onClick = { onDvarClick(dvar.id) }
                                                        )
                                                    }
                                                }
                                            } else {
                                                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                                    remaining.take(2).forEach { dvar ->
                                                        SecondaryInsightCard(
                                                            dvar = dvar,
                                                            modifier = Modifier.fillMaxWidth(),
                                                            onClick = { onDvarClick(dvar.id) }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                items(remaining.drop(2), key = { it.id }) { dvar ->
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
            borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.18f),
            selectedBorderColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f),
            borderWidth = 0.dp,
            selectedBorderWidth = 0.dp
        )
    )
}

@Composable
private fun MostRecentCard(
    title: String,
    currentParshaLabel: String?,
    mostRecent: DvarTorah?,
    onClick: (() -> Unit)?
) {
    EditorialPanel {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Most Recent",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = mostRecent?.body?.take(220)?.trim()?.let {
                    if (mostRecent.body.length > 220) "$it..." else it
                } ?: "No recent item available.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = mostRecent?.title ?: "No recent Dvar Torah yet",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = buildString {
                            append(mostRecent?.authorName ?: "Quick Dvar Torah")
                            if (!currentParshaLabel.isNullOrBlank()) {
                                append(" • ")
                                append(currentParshaLabel)
                            }
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onClick != null) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable(onClick = onClick)
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Read",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SecondaryInsightCard(
    dvar: DvarTorah,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    EditorialPanel(modifier = modifier.clickable(onClick = onClick)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = dvar.parshaOccasion?.displayNameEn ?: "Featured",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = dvar.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = dvar.body.take(120).trim().let { if (dvar.body.length > 120) "$it..." else it },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dvar.authorName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
