package com.example.vibedex.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.vibedex.data.MealPlannerRepository
import com.example.vibedex.model.DailyMealPlan
import com.example.vibedex.model.MealCandidate
import com.example.vibedex.model.MealPlannerState
import com.example.vibedex.model.MealType
import com.example.vibedex.model.DietaryTag
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

    private val requiredTags = setOf(DietaryTag.SOUTH_INDIAN, DietaryTag.DIABETES_FRIENDLY)

    fun addCandidate(type: MealType, name: String, notes: String, tags: Set<DietaryTag>) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return
        viewModelScope.launch {
            val normalizedTags = if (tags.isEmpty()) requiredTags else tags
            repository.addCandidate(
                type,
                MealCandidate(
                    name = trimmedName,
                    nutritionNotes = notes.trim(),
                    tags = normalizedTags + requiredTags
                )
            )
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
                val eligible = options.filter { candidate ->
                    requiredTags.all { it in candidate.tags } && DietaryTag.LOW_GLYCEMIC in candidate.tags
                }
                val nutrientBalanced = eligible.ifEmpty {
                    options.filter { candidate ->
                        requiredTags.all { it in candidate.tags }
                    }
                }
                val usedRecently = existingMeals[type].orEmpty().toMutableSet()
                usedRecently += results.mapNotNull { it.meals[type]?.name?.lowercase() }

                val availablePool = nutrientBalanced.ifEmpty { options }
                val available = availablePool.filterNot { option ->
                    option.name.lowercase() in usedRecently
                }

                val chosen = when {
                    available.isNotEmpty() -> available.random(random)
                    availablePool.isNotEmpty() -> availablePool.random(random)
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
        MealType.BREAKFAST -> listOf(
            MealCandidate(
                name = "Vegetable pesarattu",
                nutritionNotes = "Green gram crepe with ginger chutney",
                tags = requiredTags + DietaryTag.LOW_GLYCEMIC + DietaryTag.HIGH_PROTEIN
            )
        )
        MealType.LUNCH -> listOf(
            MealCandidate(
                name = "Thalipeeth with curd",
                nutritionNotes = "Multi-grain flatbread with probiotic curd",
                tags = requiredTags + DietaryTag.LOW_GLYCEMIC + DietaryTag.FIBER_RICH
            )
        )
        MealType.SNACK -> listOf(
            MealCandidate(
                name = "Masala sundal cup",
                nutritionNotes = "Chickpeas tempered with curry leaves",
                tags = requiredTags + DietaryTag.LOW_GLYCEMIC + DietaryTag.HIGH_PROTEIN
            )
        )
        MealType.DINNER -> listOf(
            MealCandidate(
                name = "Drumstick sambar with millet idiyappam",
                nutritionNotes = "Light evening sambar with steamed millet noodles",
                tags = requiredTags + DietaryTag.LOW_GLYCEMIC + DietaryTag.HEART_HEALTHY
            )
        )
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
