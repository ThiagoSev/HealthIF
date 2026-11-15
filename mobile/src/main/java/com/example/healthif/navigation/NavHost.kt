package com.example.healthif.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthif.ui.feature.initial.InitialScreen
import com.example.healthif.ui.feature.heartbeats.HeartCareScreen
import kotlinx.serialization.Serializable

@Serializable
object InitialRoute

@Serializable
object HeartCareRoute

@Composable
fun NavHost(paddingValues: PaddingValues) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = InitialRoute,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable<InitialRoute> {
            InitialScreen(
                navigateToHeartCareScreen = {
                    navController.navigate(HeartCareRoute)
                }
            )
        }

        composable<HeartCareRoute> {
            HeartCareScreen()
        }
    }
}


