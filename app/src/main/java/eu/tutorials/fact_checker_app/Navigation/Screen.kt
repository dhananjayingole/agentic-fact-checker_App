package eu.tutorials.fact_checker_app.Navigation

sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object History  : Screen("history")
    object Settings : Screen("settings")
    object Result   : Screen("result/{resultId}") {
        fun createRoute(resultId: Long) = "result/$resultId"
    }
}