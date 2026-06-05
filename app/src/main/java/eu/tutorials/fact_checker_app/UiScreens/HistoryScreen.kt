package eu.tutorials.fact_checker_app.UiScreens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.tutorials.fact_checker_app.domain.ClaimResult
import eu.tutorials.fact_checker_app.ui.theme.*
import eu.tutorials.fact_checker_app.viewmodels.HistoryFilter
import eu.tutorials.fact_checker_app.viewmodels.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onResultClick: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val results by viewModel.results.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Icon(Icons.Filled.DeleteForever, null) },
            title = { Text("Clear All History") },
            text = { Text("This will permanently delete all your fact-check history. This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearDialog = false
                    }
                ) { Text("Clear All", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (results.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Filled.DeleteSweep, "Clear All",
                                tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ── Search bar ─────────────────────────────────────
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search history…") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = viewModel::clearSearch) {
                            Icon(Icons.Filled.Clear, "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            // ── Filter chips ───────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(HistoryFilter.values()) { filter ->
                    FilterChipItem(
                        filter = filter,
                        isSelected = filter == activeFilter,
                        onClick = { viewModel.onFilterChanged(filter) }
                    )
                }
            }

            // ── Results ────────────────────────────────────────
            if (results.isEmpty()) {
                EmptyHistoryState(searchQuery = searchQuery)
            } else {
                Text(
                    "${results.size} result${if (results.size != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(results, key = { it.id }) { result ->
                        SwipeableHistoryItem(
                            result = result,
                            onClick = { onResultClick(result.id) },
                            onDelete = { viewModel.deleteResult(result) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// ── Filter Chip ───────────────────────────────────────────────

@Composable
private fun FilterChipItem(
    filter: HistoryFilter,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val (label, color) = when (filter) {
        HistoryFilter.ALL          -> "All" to MaterialTheme.colorScheme.primary
        HistoryFilter.TRUE         -> "True" to TrueGreen
        HistoryFilter.FALSE        -> "False" to FalseRed
        HistoryFilter.INCONCLUSIVE -> "Inconclusive" to InconclusiveAmber
        HistoryFilter.FAVORITES    -> "Bookmarked" to MaterialTheme.colorScheme.primary
    }

    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = if (filter == HistoryFilter.FAVORITES) {
            { Icon(Icons.Filled.Bookmark, null, Modifier.size(16.dp)) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = color.copy(alpha = 0.15f),
            selectedLabelColor = color
        )
    )
}

// ── Swipeable History Item ────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableHistoryItem(
    result: ClaimResult,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Filled.Delete,
                    "Delete",
                    modifier = Modifier.padding(end = 20.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        HistoryItemCard(result = result, onClick = onClick)
    }
}

// ── History Item Card ─────────────────────────────────────────

@Composable
private fun HistoryItemCard(result: ClaimResult, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Verdict circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(result.verdict.backgroundColor()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    result.verdict.icon(),
                    fontWeight = FontWeight.Bold,
                    color = result.verdict.contentColor()
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    result.claim.truncate(70),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        result.verdict.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = result.verdict.contentColor(),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text("•", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
                    Text(
                        result.timestamp.toRelativeTime(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

// ── Empty State ───────────────────────────────────────────────

@Composable
private fun EmptyHistoryState(searchQuery: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                if (searchQuery.isNotEmpty()) Icons.Filled.SearchOff else Icons.Filled.History,
                null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                if (searchQuery.isNotEmpty()) "No results for \"$searchQuery\""
                else "No fact-checks yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            if (searchQuery.isEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Start checking claims from the home screen",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }
    }
}