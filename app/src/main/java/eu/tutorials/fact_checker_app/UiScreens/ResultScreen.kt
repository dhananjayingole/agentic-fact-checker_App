package eu.tutorials.fact_checker_app.UiScreens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.tutorials.fact_checker_app.domain.ClaimResult
import eu.tutorials.fact_checker_app.domain.Evidence
import eu.tutorials.fact_checker_app.domain.Verdict
import eu.tutorials.fact_checker_app.ui.theme.FalseRed
import eu.tutorials.fact_checker_app.ui.theme.InconclusiveAmber
import eu.tutorials.fact_checker_app.ui.theme.NeutralGray
import eu.tutorials.fact_checker_app.ui.theme.TrueGreen
import eu.tutorials.fact_checker_app.viewmodels.ResultScreenState
import eu.tutorials.fact_checker_app.viewmodels.ResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    resultId: Long,
    onBack: () -> Unit,
    onVerifyAnother: () -> Unit,
    viewModel: ResultViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isFavorite by viewModel.isFavorite.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(resultId) { viewModel.loadResult(resultId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fact Check Result") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (state is ResultScreenState.Success) {
                        val result = (state as ResultScreenState.Success).result
                        IconButton(onClick = { viewModel.toggleFavorite(result.id) }) {
                            Icon(
                                if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                "Bookmark",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = {
                            val shareText = buildShareText(result)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share Result"))
                        }) {
                            Icon(Icons.Filled.Share, "Share")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        when (val s = state) {
            is ResultScreenState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ResultScreenState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.ErrorOutline, null, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text(s.message)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = onBack) { Text("Go Back") }
                    }
                }
            }

            is ResultScreenState.Success -> {
                ResultContent(
                    result = s.result,
                    padding = padding,
                    onVerifyAnother = onVerifyAnother
                )
            }
        }
    }
}

// ── Main Result Content ───────────────────────────────────────

@Composable
private fun ResultContent(
    result: ClaimResult,
    padding: PaddingValues,
    onVerifyAnother: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Claim text
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        Icons.Filled.FormatQuote,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        result.claim,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Verdict card (animated)
        item { AnimatedVerdictCard(result.verdict, result.confidenceScore) }

        // Summary
        item { SummaryCard(result.evidenceSummary) }

        // Reasoning
        item { ReasoningCard(result.reasoning) }

        // Confidence meter
        item { ConfidenceMeter(result.confidenceScore) }

        // Meta info
        item { MetaInfoRow(result) }

        // Evidence list
        if (result.evidenceList.isNotEmpty()) {
            item {
                Text(
                    "Evidence Sources (${result.evidenceCount})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            items(result.evidenceList) { evidence ->
                EvidenceCard(evidence)
            }
        }

        // Verify Another button
        item {
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onVerifyAnother,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Verify Another Claim", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Animated Verdict Card ─────────────────────────────────────

@Composable
private fun AnimatedVerdictCard(verdict: Verdict, confidence: Double) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn()
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = verdict.backgroundColor()
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Big verdict icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(verdict.contentColor().copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        verdict.icon(),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = verdict.contentColor()
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    verdict.label.uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = verdict.contentColor(),
                    letterSpacing = 3.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Confidence: ${confidence.toConfidenceLabel()} (${confidence.toConfidencePercent()}%)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = verdict.contentColor().copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ── Summary Card ──────────────────────────────────────────────

@Composable
private fun SummaryCard(summary: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Summarize,
                    null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Summary", fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            Text(summary, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
        }
    }
}

// ── Reasoning Card ────────────────────────────────────────────

@Composable
private fun ReasoningCard(reasoning: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Psychology,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("AI Reasoning", fontWeight = FontWeight.SemiBold)
                }
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = null
                )
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        reasoning,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 22.sp
                    )
                }
            }
            if (!expanded) {
                Spacer(Modifier.height(4.dp))
                Text(
                    reasoning,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ── Confidence Meter ──────────────────────────────────────────

@Composable
private fun ConfidenceMeter(score: Double) {
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val progress by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(1200, easing = EaseOutCubic),
        label = "confidence"
    )

    LaunchedEffect(Unit) { animatedProgress = score.toConfidencePercent() / 100f }

    val color = when {
        score >= 7.0 -> TrueGreen
        score <= 3.0 -> FalseRed
        else -> InconclusiveAmber
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Speed,
                        null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Confidence Score", fontWeight = FontWeight.SemiBold)
                }
                Text(
                    "${score.toConfidencePercent()}%",
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 18.sp
                )
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = color,
                trackColor = color.copy(alpha = 0.15f),
                strokeCap = StrokeCap.Round
            )
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Definitely False", style = MaterialTheme.typography.labelSmall, color = FalseRed)
                Text("Definitely True", style = MaterialTheme.typography.labelSmall, color = TrueGreen)
            }
        }
    }
}

// ── Meta Info Row ─────────────────────────────────────────────

@Composable
private fun MetaInfoRow(result: ClaimResult) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetaChip(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Link,
            label = "Sources",
            value = result.sourcesSearched.toString()
        )
        MetaChip(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Article,
            label = "Evidence",
            value = result.evidenceCount.toString()
        )
        MetaChip(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Timer,
            label = "Time",
            value = "${result.processingTimeSeconds}s"
        )
    }
}

@Composable
private fun MetaChip(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(label, style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

// ── Evidence Card ─────────────────────────────────────────────

@Composable
private fun EvidenceCard(evidence: Evidence) {
    val stanceColor = when (evidence.stance) {
        "supports"    -> TrueGreen
        "contradicts" -> FalseRed
        else          -> NeutralGray
    }
    val credibilityColor = when (evidence.sourceCredibility) {
        "high"   -> TrueGreen
        "medium" -> InconclusiveAmber
        else     -> FalseRed
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Title + source type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    evidence.title,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                // Source type badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (evidence.sourceType == "wikipedia")
                                Color(0xFF2E7D32).copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        if (evidence.sourceType == "wikipedia") "Wiki" else "Web",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (evidence.sourceType == "wikipedia") Color(0xFF2E7D32)
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(6.dp))
            Text(
                evidence.snippet.truncate(160),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(10.dp))

            // Stance + credibility + relevance chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallChip(
                    label = evidence.stance.replaceFirstChar { it.uppercase() },
                    color = stanceColor
                )
                SmallChip(
                    label = "${evidence.sourceCredibility.replaceFirstChar { it.uppercase() }} credibility",
                    color = credibilityColor
                )
                SmallChip(
                    label = "${(evidence.relevanceScore * 100).toInt()}% relevant",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun SmallChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(99.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}

// ── Share text builder ────────────────────────────────────────

private fun buildShareText(result: ClaimResult): String = buildString {
    appendLine("🔍 FACT CHECK RESULT")
    appendLine()
    appendLine("Claim: \"${result.claim}\"")
    appendLine()
    appendLine("Verdict: ${result.verdict.label.uppercase()} ${result.verdict.emoji}")
    appendLine("Confidence: ${result.confidenceScore.toConfidencePercent()}%")
    appendLine()
    appendLine("Summary: ${result.evidenceSummary}")
    appendLine()
    appendLine("Checked with FactChecker AI")
}