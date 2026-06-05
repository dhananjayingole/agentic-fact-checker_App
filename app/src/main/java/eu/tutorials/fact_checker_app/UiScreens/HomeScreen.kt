package eu.tutorials.fact_checker_app.UiScreens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.tutorials.fact_checker_app.domain.ClaimResult
import eu.tutorials.fact_checker_app.domain.VerifyState
import eu.tutorials.fact_checker_app.ui.theme.FalseRed
import eu.tutorials.fact_checker_app.ui.theme.TrueGreen
import eu.tutorials.fact_checker_app.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onResultReady: (Long) -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val verifyState by viewModel.verifyState.collectAsStateWithLifecycle()
    val claimText by viewModel.claimText.collectAsStateWithLifecycle()
    val recentChecks by viewModel.recentChecks.collectAsStateWithLifecycle()
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val keyboard = LocalSoftwareKeyboardController.current

    // Navigate when result is ready
    LaunchedEffect(verifyState) {
        if (verifyState is VerifyState.Success) {
            val result = (verifyState as VerifyState.Success).result
            onResultReady(result.id)
            viewModel.resetState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "FactChecker AI",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onHistoryClick) {
                        Icon(Icons.Filled.History, "History")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Hero banner ──────────────────────────────────
            item {
                HeroBanner()
            }

            // ── Stats row ────────────────────────────────────
            if (stats.total > 0) {
                item {
                    StatsRow(
                        total = stats.total,
                        truePercent = stats.truePercent,
                        falsePercent = stats.falsePercent
                    )
                }
            }

            // ── Input card ───────────────────────────────────
            item {
                ClaimInputCard(
                    claimText = claimText,
                    onClaimChanged = viewModel::onClaimChanged,
                    onVerify = {
                        keyboard?.hide()
                        viewModel.verifyClaim()
                    },
                    onClear = viewModel::clearInput,
                    isLoading = verifyState is VerifyState.Loading,
                    error = (verifyState as? VerifyState.Error)?.message
                )
            }

            // ── Loading indicator ────────────────────────────
            if (verifyState is VerifyState.Loading) {
                item { VerifyingIndicator() }
            }

            // ── Recent checks ────────────────────────────────
            if (recentChecks.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Checks",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        TextButton(onClick = onHistoryClick) {
                            Text("See All")
                            Icon(
                                Icons.Filled.ChevronRight,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                items(recentChecks) { result ->
                    RecentCheckCard(result = result, onClick = { onResultReady(result.id) })
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ── Hero Banner ───────────────────────────────────────────────

@Composable
private fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Text(
                "AI Fact Checker",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Verify any claim instantly using AI\nand multi-source evidence",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
        Icon(
            Icons.Filled.Shield,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(64.dp),
            tint = Color.White.copy(alpha = 0.15f)
        )
    }
}

// ── Stats Row ─────────────────────────────────────────────────

@Composable
private fun StatsRow(total: Int, truePercent: Int, falsePercent: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatChip(
            modifier = Modifier.weight(1f),
            label = "Total",
            value = total.toString(),
            color = MaterialTheme.colorScheme.primary
        )
        StatChip(
            modifier = Modifier.weight(1f),
            label = "True",
            value = "$truePercent%",
            color = TrueGreen
        )
        StatChip(
            modifier = Modifier.weight(1f),
            label = "False",
            value = "$falsePercent%",
            color = FalseRed
        )
    }
}

@Composable
private fun StatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = color.copy(alpha = 0.8f))
        }
    }
}

// ── Claim Input Card ──────────────────────────────────────────

@Composable
private fun ClaimInputCard(
    claimText: String,
    onClaimChanged: (String) -> Unit,
    onVerify: () -> Unit,
    onClear: () -> Unit,
    isLoading: Boolean,
    error: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Enter a Claim to Verify",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = claimText,
                onValueChange = onClaimChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "e.g. Humans only use 10% of their brain",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                },
                trailingIcon = {
                    if (claimText.isNotEmpty()) {
                        IconButton(onClick = onClear) {
                            Icon(Icons.Filled.Clear, "Clear")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onVerify() }),
                minLines = 2,
                maxLines = 4,
                isError = error != null,
                supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = claimText.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Verifying…")
                } else {
                    Icon(Icons.Filled.Search, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Verify Claim", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// ── Verifying Indicator ───────────────────────────────────────

@Composable
private fun VerifyingIndicator() {
    val steps = listOf("Searching web…", "Analysing evidence…", "Generating verdict…")
    var currentStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1800)
            currentStep = (currentStep + 1) % steps.size
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(12.dp))
            AnimatedContent(
                targetState = steps[currentStep],
                transitionSpec = {
                    fadeIn() + slideInVertically { it } togetherWith
                            fadeOut() + slideOutVertically { -it }
                },
                label = "step"
            ) { step ->
                Text(step, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// ── Recent Check Card ─────────────────────────────────────────

@Composable
private fun RecentCheckCard(result: ClaimResult, onClick: () -> Unit) {
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
            // Verdict badge
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(result.verdict.backgroundColor()),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    result.verdict.icon(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = result.verdict.contentColor()
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    result.claim.truncate(60),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    result.timestamp.toRelativeTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}