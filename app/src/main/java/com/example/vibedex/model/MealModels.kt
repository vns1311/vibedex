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
enum class DietaryTag(val label: String) {
    SOUTH_INDIAN("South Indian"),
    DIABETES_FRIENDLY("Diabetes-friendly"),
    LOW_GLYCEMIC("Low GI"),
    HIGH_PROTEIN("High protein"),
    FIBER_RICH("Fiber rich"),
    HEART_HEALTHY("Heart healthy")
}

@Serializable
data class MealCandidate(
    val name: String,
    val nutritionNotes: String = "",
    val tags: Set<DietaryTag> = setOf(
        DietaryTag.SOUTH_INDIAN,
        DietaryTag.DIABETES_FRIENDLY,
        DietaryTag.LOW_GLYCEMIC
    )
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
        MealCandidate(
            name = "Ragi idli with sambar",
            nutritionNotes = "Fermented millet idli served with vegetable-packed lentil stew",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.FIBER_RICH,
                DietaryTag.HEART_HEALTHY
            )
        ),
        MealCandidate(
            name = "Vegetable rava upma",
            nutritionNotes = "Semolina cooked with carrots, beans, peas, and tempered spices",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.FIBER_RICH
            )
        ),
        MealCandidate(
            name = "Adai dosa with aviyal",
            nutritionNotes = "Protein-rich mixed lentil crepe with coconut vegetable curry",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HIGH_PROTEIN,
                DietaryTag.HEART_HEALTHY
            )
        ),
        MealCandidate(
            name = "Millet pongal with pepper rasam",
            nutritionNotes = "Foxtail millet pongal tempered with curry leaves and served with light rasam",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HIGH_PROTEIN,
                DietaryTag.FIBER_RICH
            )
        ),
        MealCandidate(
            name = "Sprouted moong sundal",
            nutritionNotes = "Tempered sprouts with grated coconut and raw mango",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HIGH_PROTEIN,
                DietaryTag.FIBER_RICH
            )
        )
    )
    MealType.LUNCH -> listOf(
        MealCandidate(
            name = "Brown rice sambar with keerai poriyal",
            nutritionNotes = "Short grain brown rice, lentil stew, and sautéed greens",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HEART_HEALTHY,
                DietaryTag.FIBER_RICH
            )
        ),
        MealCandidate(
            name = "Vegetable kurma with phulka",
            nutritionNotes = "Light coconut kurma paired with whole wheat phulkas",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HEART_HEALTHY
            )
        ),
        MealCandidate(
            name = "Kootu with red rice",
            nutritionNotes = "Bottle gourd kootu, steamed red rice, and cucumber thayir pachadi",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.FIBER_RICH
            )
        ),
        MealCandidate(
            name = "Millet lemon rice with peanut podi",
            nutritionNotes = "Barnyard millet tempered with lemon, curry leaves, and roasted peanuts",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HEART_HEALTHY
            )
        ),
        MealCandidate(
            name = "Rajma sundal salad",
            nutritionNotes = "Kidney beans tossed with coconut, raw veggies, and lime",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HIGH_PROTEIN,
                DietaryTag.FIBER_RICH
            )
        )
    )
    MealType.SNACK -> listOf(
        MealCandidate(
            name = "Buttermilk with roasted chana",
            nutritionNotes = "Spiced neer mor with a side of roasted gram",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HEART_HEALTHY
            )
        ),
        MealCandidate(
            name = "Sprouted sundal bowl",
            nutritionNotes = "Channa and green gram sprouts tempered with mustard and coconut",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HIGH_PROTEIN,
                DietaryTag.FIBER_RICH
            )
        ),
        MealCandidate(
            name = "Carrot cucumber kosambari",
            nutritionNotes = "Grated veggies with soaked moong dal and lemon",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HEART_HEALTHY,
                DietaryTag.FIBER_RICH
            )
        ),
        MealCandidate(
            name = "Bajra laddu (no sugar)",
            nutritionNotes = "Finger millet balls bound with dates and nuts",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HEART_HEALTHY
            )
        ),
        MealCandidate(
            name = "Steamed kollu (horse gram) sundal",
            nutritionNotes = "Horse gram tempered with ginger and coconut",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HIGH_PROTEIN,
                DietaryTag.FIBER_RICH
            )
        )
    )
    MealType.DINNER -> listOf(
        MealCandidate(
            name = "Thinai (foxtail millet) vegetable pulao",
            nutritionNotes = "Millet pulao with beans, cauliflower, and peas",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.FIBER_RICH
            )
        ),
        MealCandidate(
            name = "Paneer chettinad with millet rotis",
            nutritionNotes = "Paneer simmered in chettinad spices served with multi-millet rotis",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HIGH_PROTEIN,
                DietaryTag.HEART_HEALTHY
            )
        ),
        MealCandidate(
            name = "Vegetable stew with appam",
            nutritionNotes = "Coconut milk stew with vegetables and fermented appam",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HEART_HEALTHY
            )
        ),
        MealCandidate(
            name = "Kambu koozh with side salad",
            nutritionNotes = "Fermented pearl millet porridge with raw veggie salad",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.FIBER_RICH
            )
        ),
        MealCandidate(
            name = "Masala grilled fish with sautéed greens",
            nutritionNotes = "Seer fish grilled with spices and served with amaranth greens",
            tags = setOf(
                DietaryTag.SOUTH_INDIAN,
                DietaryTag.DIABETES_FRIENDLY,
                DietaryTag.LOW_GLYCEMIC,
                DietaryTag.HEART_HEALTHY,
                DietaryTag.HIGH_PROTEIN
            )
        )
    )
}
