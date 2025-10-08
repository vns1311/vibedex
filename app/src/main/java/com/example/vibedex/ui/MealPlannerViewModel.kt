package com.example.vibedex.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vibedex.data.MealPlannerRepository
import com.example.vibedex.model.DailyMealPlan
import com.example.vibedex.model.MealCandidate
import com.example.vibedex.model.MealPlannerState
import com.example.vibedex.model.MealType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

class MealPlannerViewModel(private val repository: MealPlannerRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(MealPlannerUiState())
    val uiState: StateFlow<MealPlannerUiState> = _uiState.asStateFlow()

    private val random = Random(System.currentTimeMillis())

    init {
        viewModelScope.launch {
            repository.state.collect { state ->
                _uiState.update { current ->
                    current.copy(isLoading = false, plannerState = state)
                }
            }
        }
    }

    fun addCandidate(type: MealType, name: String, notes: String) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return
        viewModelScope.launch {
            repository.addCandidate(type, MealCandidate(trimmedName, notes.trim()))
        }
    }

    fun removeCandidate(type: MealType, candidate: MealCandidate) {
        viewModelScope.launch {
            repository.removeCandidate(type, candidate)
        }
    }

    fun generatePlan(daysToGenerate: Int = 7) {
        val currentState = _uiState.value.plannerState
        val plan = buildPlan(daysToGenerate, currentState)
        viewModelScope.launch {
            repository.savePlan(plan)
        }
    }

    private fun buildPlan(daysToGenerate: Int, state: MealPlannerState): List<DailyMealPlan> {
        val candidates = MealType.defaultOrder.associateWith { type ->
            state.candidates[type].orEmpty().ifEmpty { defaultFallback(type) }
        }

        val existingMeals = state.planHistory.flatMap { plan ->
            plan.days.flatMap { day -> day.meals.entries.map { it.key to it.value.name.lowercase() } }
        }.groupBy({ it.first }, { it.second })

        val startDate = LocalDate.now()
        val results = mutableListOf<DailyMealPlan>()
        val dailyHistory = mutableSetOf<String>()

        repeat(daysToGenerate) { index ->
            val date = startDate.plusDays(index.toLong())
            val mealsForDay = mutableMapOf<MealType, MealCandidate>()

            MealType.defaultOrder.forEach { type ->
                val options = candidates[type].orEmpty()
                val usedRecently = existingMeals[type].orEmpty().toMutableSet()
                usedRecently += results.mapNotNull { it.meals[type]?.name?.lowercase() }

                val available = options.filterNot { option ->
                    option.name.lowercase() in usedRecently
                }

                val chosen = when {
                    available.isNotEmpty() -> available.random(random)
                    options.isNotEmpty() -> options.random(random)
                    else -> MealCandidate("Add options for ${type.displayName}")
                }
                mealsForDay[type] = chosen
            }

            val signature = mealsForDay.values.joinToString { it.name.lowercase() }
            if (signature !in dailyHistory) {
                dailyHistory += signature
                results += DailyMealPlan(
                    date = date.toString(),
                    meals = mealsForDay
                )
            }
        }
        return results
    }

    private fun defaultFallback(type: MealType): List<MealCandidate> = when (type) {
        MealType.BREAKFAST -> listOf(MealCandidate("Fresh fruit bowl", "Natural sugars"))
        MealType.LUNCH -> listOf(MealCandidate("Seasonal grain bowl", "Balanced macros"))
        MealType.SNACK -> listOf(MealCandidate("Yogurt and seeds", "Protein boost"))
        MealType.DINNER -> listOf(MealCandidate("Roasted veggies & beans", "Plant power"))
    }
}

data class MealPlannerUiState(
    val isLoading: Boolean = true,
    val plannerState: MealPlannerState = MealPlannerState()
)

class MealPlannerViewModelFactory(
    private val repository: MealPlannerRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealPlannerViewModel::class.java)) {
            return MealPlannerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
