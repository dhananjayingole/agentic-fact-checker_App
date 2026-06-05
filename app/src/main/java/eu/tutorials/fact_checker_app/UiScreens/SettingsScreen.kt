package eu.tutorials.fact_checker_app.UiScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.tutorials.fact_checker_app.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val apiBaseUrl by viewModel.apiBaseUrl.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val maxSources by viewModel.maxSources.collectAsStateWithLifecycle()

    var apiUrlInput by remember(apiBaseUrl) { mutableStateOf(apiBaseUrl) }
    var showClearDialog by remember { mutableStateOf(false) }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            icon = { Icon(Icons.Filled.DeleteForever, null) },
            title = { Text("Clear History") },
            text = { Text("Delete all fact-check history? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearHistory()
                    showClearDialog = false
                }) { Text("Clear", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── API Settings ───────────────────────────────────
            SettingsSection(title = "API Configuration") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = apiUrlInput,
                        onValueChange = { apiUrlInput = it },
                        label = { Text("API Base URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Filled.Link, null) },
                        supportingText = { Text("Your deployed Render/Railway URL") }
                    )
                    Button(
                        onClick = { viewModel.setApiBaseUrl(apiUrlInput) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Save, null, Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Save URL")
                    }
                }
            }

            // ── Verification settings ──────────────────────────
            SettingsSection(title = "Verification") {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Max Sources", fontWeight = FontWeight.Medium)
                            Text(
                                "Sources searched per claim: $maxSources",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                        Text(
                            maxSources.toString(),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = maxSources.toFloat(),
                        onValueChange = { viewModel.setMaxSources(it.toInt()) },
                        valueRange = 1f..10f,
                        steps = 8,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1 (Fast)", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        Text("10 (Thorough)", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                    }
                }
            }

            // ── Appearance ─────────────────────────────────────
            SettingsSection(title = "Appearance") {
                SettingsToggleRow(
                    icon = Icons.Filled.DarkMode,
                    title = "Dark Theme",
                    subtitle = "Switch between light and dark mode",
                    checked = isDarkTheme,
                    onCheckedChange = viewModel::setDarkTheme
                )
            }

            // ── Data ───────────────────────────────────────────
            SettingsSection(title = "Data") {
                OutlinedButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Filled.DeleteSweep, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Clear All History")
                }
            }

            // ── About ──────────────────────────────────────────
            SettingsSection(title = "About") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AboutRow(icon = Icons.Filled.Info, label = "Version", value = "1.0.0")
                    AboutRow(icon = Icons.Filled.Code, label = "Stack",
                        value = "Kotlin · Jetpack Compose · FastAPI · Groq LLM")
                    AboutRow(icon = Icons.Filled.Cloud, label = "Backend", value = "Render (Free tier)")
                    AboutRow(icon = Icons.Filled.Psychology, label = "AI Model",
                        value = "Llama 3.3 70B via Groq")
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Settings Section wrapper ──────────────────────────────────

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

// ── Toggle Row ────────────────────────────────────────────────

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Medium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// ── About Row ─────────────────────────────────────────────────

@Composable
private fun AboutRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, null, modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Text(value, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
        }
    }
}