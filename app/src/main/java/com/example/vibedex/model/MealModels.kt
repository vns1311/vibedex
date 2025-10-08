package com.example.vibedex.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
enum class MealType(val displayName: String) {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    SNACK("Snack"),
    DINNER("Dinner");

    companion object {
        val defaultOrder = values().toList()
    }
}

@Serializable
data class MealCandidate(
    val name: String,
    val nutritionNotes: String = ""
)

@Serializable
data class DailyMealPlan(
    val date: String,
    val meals: Map<MealType, MealCandidate>
) {
    val formattedDate: String
        get() = try {
            LocalDate.parse(date).format(DateTimeFormatter.ofPattern("EEE, MMM d"))
        } catch (_: Exception) {
            date
        }
}

@Serializable
data class MealPlan(
    val id: String,
    val generatedOn: String,
    val days: List<DailyMealPlan>
)

@Serializable
data class MealPlannerState(
    val candidates: Map<MealType, List<MealCandidate>> = MealType.defaultOrder.associateWith { defaultCandidates(it) },
    val planHistory: List<MealPlan> = emptyList()
)

fun defaultCandidates(type: MealType): List<MealCandidate> = when (type) {
    MealType.BREAKFAST -> listOf(
        MealCandidate("Oatmeal with berries", "Fiber-rich carbs"),
        MealCandidate("Greek yogurt parfait", "High protein"),
        MealCandidate("Veggie omelette", "Protein and veggies")
    )
    MealType.LUNCH -> listOf(
        MealCandidate("Quinoa salad", "Complex carbs + veggies"),
        MealCandidate("Grilled chicken wrap", "Lean protein"),
        MealCandidate("Lentil soup", "Plant-based protein")
    )
    MealType.SNACK -> listOf(
        MealCandidate("Hummus with veggies", "Healthy fats"),
        MealCandidate("Mixed nuts", "Satiating fats"),
        MealCandidate("Apple with peanut butter", "Protein + fiber")
    )
    MealType.DINNER -> listOf(
        MealCandidate("Baked salmon with greens", "Omega-3 rich"),
        MealCandidate("Stir fry tofu and veggies", "Plant protein"),
        MealCandidate("Turkey meatballs with zucchini", "Lean protein")
    )
}
