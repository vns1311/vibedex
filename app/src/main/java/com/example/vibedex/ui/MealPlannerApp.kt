package com.example.vibedex.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.vibedex.R
import com.example.vibedex.model.DailyMealPlan
import com.example.vibedex.model.MealCandidate
import com.example.vibedex.model.MealPlan
import com.example.vibedex.model.MealPlannerState
import com.example.vibedex.model.MealType
import com.example.vibedex.ui.theme.MealPlannerTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerApp(viewModel: MealPlannerViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            GeneratePlanButton(onGenerate = {
                if (uiState.plannerState.candidates.values.any { it.isNotEmpty() }) {
                    viewModel.generatePlan()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("New plan generated")
                    }
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Add meal candidates first")
                    }
                }
            })
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            if (uiState.isLoading) {
                LoadingScreen()
            } else {
                MealPlannerContent(
                    state = uiState,
                    onAddCandidate = viewModel::addCandidate,
                    onRemoveCandidate = viewModel::removeCandidate
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Loading meal plans...")
    }
}

@Composable
private fun MealPlannerContent(
    state: MealPlannerUiState,
    onAddCandidate: (MealType, String, String) -> Unit,
    onRemoveCandidate: (MealType, MealCandidate) -> Unit
) {
    val addDialogState = remember { mutableStateOf<MealType?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            SectionTitle(text = stringResource(id = R.string.meal_candidates))
        }
        items(MealType.defaultOrder) { type ->
            MealTypeCard(
                type = type,
                candidates = state.plannerState.candidates[type].orEmpty(),
                onAddCandidate = { addDialogState.value = type },
                onRemoveCandidate = { candidate -> onRemoveCandidate(type, candidate) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionTitle(text = stringResource(id = R.string.plan_history))
        }
        items(state.plannerState.planHistory) { plan ->
            PlanHistoryCard(plan = plan)
        }
    }

    addDialogState.value?.let { type ->
        AddCandidateDialog(
            mealType = type,
            onDismiss = { addDialogState.value = null },
            onAdd = { name, notes ->
                onAddCandidate(type, name, notes)
                addDialogState.value = null
            }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun MealPlannerContentPreview() {
    val previewCandidates = MealType.defaultOrder.associateWith { type ->
        listOf(
            MealCandidate("${type.displayName} idea 1", "Wholesome option"),
            MealCandidate("${type.displayName} idea 2")
        )
    }

    val previewPlanHistory = listOf(
        MealPlan(
            id = "plan-1",
            generatedOn = "2023-08-01",
            days = listOf(
                DailyMealPlan(
                    date = "2023-08-01",
                    meals = MealType.defaultOrder.associateWith { type ->
                        MealCandidate("${type.displayName} sampler", "Sample notes")
                    }
                )
            )
        )
    )

    val previewState = MealPlannerUiState(
        isLoading = false,
        plannerState = MealPlannerState(
            candidates = previewCandidates,
            planHistory = previewPlanHistory
        )
    )

    MealPlannerTheme {
        MealPlannerContent(
            state = previewState,
            onAddCandidate = { _, _, _ -> },
            onRemoveCandidate = { _, _ -> }
        )
    }
}

@Composable
private fun MealTypeCard(
    type: MealType,
    candidates: List<MealCandidate>,
    onAddCandidate: () -> Unit,
    onRemoveCandidate: (MealCandidate) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (candidates.isEmpty()) {
            Text(text = "No candidates yet. Add your favorites!")
        } else {
            candidates.forEach { candidate ->
                CandidateRow(candidate = candidate, onRemoveCandidate = onRemoveCandidate)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onAddCandidate) {
            Text(text = "Add ${type.displayName}")
        }
    }
}

@Composable
private fun CandidateRow(
    candidate: MealCandidate,
    onRemoveCandidate: (MealCandidate) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            RowWithRemove(
                title = candidate.name,
                subtitle = candidate.nutritionNotes,
                onRemove = { onRemoveCandidate(candidate) }
            )
        }
    }
}

@Composable
private fun RowWithRemove(
    title: String,
    subtitle: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            if (subtitle.isNotBlank()) {
                Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Remove"
            )
        }
    }
}

@Composable
private fun PlanHistoryCard(plan: MealPlan) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Generated on ${plan.generatedOn}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            plan.days.forEach { day ->
                DailyPlanRow(day)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DailyPlanRow(day: DailyMealPlan) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = day.formattedDate, fontWeight = FontWeight.SemiBold)
        MealType.defaultOrder.forEach { type ->
            val meal = day.meals[type]
            if (meal != null) {
                Text(text = "${type.displayName}: ${meal.name}")
                if (meal.nutritionNotes.isNotBlank()) {
                    Text(text = meal.nutritionNotes, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun GeneratePlanButton(onGenerate: () -> Unit) {
    Button(onClick = onGenerate) {
        Text(text = stringResource(id = R.string.generate_plan))
    }
}

@Composable
private fun AddCandidateDialog(
    mealType: MealType,
    onDismiss: () -> Unit,
    onAdd: (String, String) -> Unit
) {
    val nameState = remember { mutableStateOf("") }
    val notesState = remember { mutableStateOf("") }
    val errorState = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add ${mealType.displayName}") },
        text = {
            Column {
                OutlinedTextField(
                    value = nameState.value,
                    onValueChange = {
                        nameState.value = it
                        errorState.value = false
                    },
                    label = { Text("Name") },
                    isError = errorState.value
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = notesState.value,
                    onValueChange = { notesState.value = it },
                    label = { Text("Nutrition notes (optional)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nameState.value.isBlank()) {
                    errorState.value = true
                } else {
                    onAdd(nameState.value, notesState.value)
                    nameState.value = ""
                    notesState.value = ""
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
