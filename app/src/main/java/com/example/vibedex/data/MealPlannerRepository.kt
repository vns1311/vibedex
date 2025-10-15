package com.example.vibedex.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.vibedex.model.DailyMealPlan
import com.example.vibedex.model.MealCandidate
import com.example.vibedex.model.MealPlan
import com.example.vibedex.model.MealPlannerState
import com.example.vibedex.model.MealType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

private val Context.mealPlannerDataStore: DataStore<Preferences> by preferencesDataStore(name = "meal_planner")

class MealPlannerRepository(context: Context) {
    private val dataStore = context.mealPlannerDataStore

    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

    private val stateKey = stringPreferencesKey("state")

    val state: Flow<MealPlannerState> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[stateKey]?.let { json.decodeFromString(MealPlannerState.serializer(), it) }
                ?: MealPlannerState()
        }

    suspend fun addCandidate(type: MealType, candidate: MealCandidate) {
        updateState { current ->
            val updatedList = (current.candidates[type].orEmpty() + candidate)
                .distinctBy { it.name.lowercase() }
            current.copy(candidates = current.candidates + (type to updatedList))
        }
    }

    suspend fun removeCandidate(type: MealType, candidate: MealCandidate) {
        updateState { current ->
            val updatedList = current.candidates[type].orEmpty()
                .filterNot { it.name.equals(candidate.name, ignoreCase = true) }
            current.copy(candidates = current.candidates + (type to updatedList))
        }
    }

    suspend fun savePlan(days: List<DailyMealPlan>) {
        updateState { current ->
            val plan = MealPlan(
                id = UUID.randomUUID().toString(),
                generatedOn = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
                days = days
            )
            val updatedHistory = (listOf(plan) + current.planHistory).take(12)
            current.copy(planHistory = updatedHistory)
        }
    }

    suspend fun replaceCandidates(type: MealType, candidates: List<MealCandidate>) {
        updateState { current ->
            current.copy(candidates = current.candidates + (type to candidates))
        }
    }

    private suspend fun updateState(transform: (MealPlannerState) -> MealPlannerState) {
        dataStore.edit { prefs ->
            val current = prefs[stateKey]?.let {
                json.decodeFromString(MealPlannerState.serializer(), it)
            } ?: MealPlannerState()
            val updated = transform(current)
            prefs[stateKey] = json.encodeToString(MealPlannerState.serializer(), updated)
        }
    }
}
