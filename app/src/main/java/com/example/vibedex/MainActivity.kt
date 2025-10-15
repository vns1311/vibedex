package com.example.vibedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.vibedex.data.MealPlannerRepository
import com.example.vibedex.ui.MealPlannerApp
import com.example.vibedex.ui.MealPlannerViewModel
import com.example.vibedex.ui.MealPlannerViewModelFactory
import com.example.vibedex.ui.theme.VibeDexTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MealPlannerViewModel by viewModels {
        MealPlannerViewModelFactory(
            MealPlannerRepository(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VibeDexTheme {
                MealPlannerApp(viewModel)
            }
        }
    }
}
