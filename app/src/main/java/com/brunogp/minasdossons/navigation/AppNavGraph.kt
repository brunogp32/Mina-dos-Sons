package com.brunogp.minasdossons.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.ui.screens.CardCollectionScreen
import com.brunogp.minasdossons.ui.screens.ChestOpeningScreen
import com.brunogp.minasdossons.ui.screens.DebugQaScreen
import com.brunogp.minasdossons.ui.screens.GameSessionScreen
import com.brunogp.minasdossons.ui.screens.HomeScreen
import com.brunogp.minasdossons.ui.screens.ParentModeScreen
import com.brunogp.minasdossons.ui.screens.ProgressScreen
import com.brunogp.minasdossons.ui.screens.RecordingScreen
import com.brunogp.minasdossons.ui.screens.SessionResultScreen
import com.brunogp.minasdossons.ui.screens.SettingsScreen
import com.brunogp.minasdossons.ui.screens.ThroatTrainingScreen
import com.brunogp.minasdossons.ui.screens.WorldMapScreen

@Composable
fun AppNavGraph(vm: AppViewModel = viewModel()) {
    val progress by vm.progress.collectAsState()
    val density = LocalDensity.current
    val appDensity =
        if (progress.largeText) {
            Density(density = density.density, fontScale = density.fontScale * 1.15f)
        } else {
            density
        }
    val nav = rememberNavController()
    CompositionLocalProvider(LocalDensity provides appDensity) {
        NavHost(navController = nav, startDestination = "home") {
            composable("home") { HomeScreen(vm, nav) }
            composable("map") { WorldMapScreen(vm, nav) }
            composable(
                "game/{world}/{level}",
                arguments = listOf(navArgument("world") { type = NavType.IntType }, navArgument("level") { type = NavType.IntType }),
            ) {
                GameSessionScreen(vm, nav, it.arguments?.getInt("world") ?: 1, it.arguments?.getInt("level") ?: 1)
            }
            composable("throat") { ThroatTrainingScreen(vm, nav) }
            composable("recording") { RecordingScreen(vm, nav) }
            composable("cards") { CardCollectionScreen(vm, nav) }
            composable("rewards") { CardCollectionScreen(vm, nav) }
            composable("chest") { ChestOpeningScreen(vm, nav) }
            composable("album") { CardCollectionScreen(vm, nav) }
            composable("progress") { ProgressScreen(vm, nav) }
            composable("parents") { ParentModeScreen(vm, nav) }
            composable("settings") { SettingsScreen(vm, nav) }
            composable("debugqa") { DebugQaScreen(vm, nav) }
            composable(
                "result/{stars}/{world}/{level}",
                arguments =
                listOf(
                    navArgument("stars") { type = NavType.IntType },
                    navArgument("world") { type = NavType.IntType },
                    navArgument("level") {
                        type =
                            NavType.IntType
                    },
                ),
            ) {
                SessionResultScreen(
                    vm,
                    nav,
                    it.arguments?.getInt("stars") ?: 0,
                    it.arguments?.getInt("world") ?: 1,
                    it.arguments?.getInt("level") ?: 1,
                )
            }
        }
    }
}
