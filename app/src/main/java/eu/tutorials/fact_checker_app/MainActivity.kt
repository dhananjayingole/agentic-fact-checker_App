package eu.tutorials.fact_checker_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import eu.tutorials.fact_checker_app.Navigation.Screen
import eu.tutorials.fact_checker_app.UiScreens.HistoryScreen
import eu.tutorials.fact_checker_app.UiScreens.HomeScreen
import eu.tutorials.fact_checker_app.UiScreens.ResultScreen
import eu.tutorials.fact_checker_app.UiScreens.SettingsScreen
import eu.tutorials.fact_checker_app.data.repository.UserPreferencesDataStore
import eu.tutorials.fact_checker_app.ui.theme.Fact_checker_appTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefsDataStore: UserPreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isDarkTheme = runBlocking { prefsDataStore.isDarkTheme.first() }

        setContent {
            val darkTheme by prefsDataStore.isDarkTheme.collectAsState(initial = isDarkTheme)

            Fact_checker_appTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onResultReady = { resultId ->
                                navController.navigate(Screen.Result.createRoute(resultId))
                            },
                            onHistoryClick = {
                                navController.navigate(Screen.History.route)
                            },
                            onSettingsClick = {
                                navController.navigate(Screen.Settings.route)
                            }
                        )
                    }

                    composable(
                        route = Screen.Result.route,
                        arguments = listOf(navArgument("resultId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val resultId = backStackEntry.arguments?.getLong("resultId") ?: 0L
                        ResultScreen(
                            resultId = resultId,
                            onBack = { navController.popBackStack() },
                            onVerifyAnother = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.History.route) {
                        HistoryScreen(
                            onResultClick = { resultId ->
                                navController.navigate(Screen.Result.createRoute(resultId))
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
