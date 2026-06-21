package com.rohan.streaky.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

sealed class Screen(val route: String) {
    data object Splash   : Screen("splash")
    data object Today    : Screen("today")
    data object Habits   : Screen("habits")
    data object Dashboard: Screen("dashboard")
    data object Settings : Screen("settings")
    data object AddHabit : Screen("add_habit")
    data object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(id: Long) = "habit_detail/$id"
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    val bottomNavItems = listOf(
        NavItem(Screen.Today.route,     "Today",     Icons.Filled.Home,     Icons.Outlined.Home),
        NavItem(Screen.Habits.route,    "Habits",    Icons.Filled.CheckBox, Icons.Outlined.CheckBoxOutlineBlank),
        NavItem(Screen.Dashboard.route, "Dashboard", Icons.Filled.BarChart, Icons.Outlined.BarChart),
        NavItem(Screen.Settings.route,  "Settings",  Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit  = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = androidx.compose.ui.unit.Dp(0f)
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor   = MaterialTheme.colorScheme.primary,
                                selectedTextColor   = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding),
            enterTransition  = { fadeIn(tween(220)) + slideInHorizontally { it / 4 } },
            exitTransition   = { fadeOut(tween(220)) + slideOutHorizontally { -it / 4 } },
            popEnterTransition  = { fadeIn(tween(220)) + slideInHorizontally { -it / 4 } },
            popExitTransition   = { fadeOut(tween(220)) + slideOutHorizontally { it / 4 } }
        ) {
            composable(Screen.Splash.route) {
                com.rohan.streaky.ui.screens.splash.SplashScreen(
                    onFinished = {
                        navController.navigate(Screen.Today.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Today.route) {
                com.rohan.streaky.ui.screens.home.HomeScreen(
                    onAddHabit = { navController.navigate(Screen.AddHabit.route) },
                    onHabitClick = { id -> navController.navigate(Screen.HabitDetail.createRoute(id)) }
                )
            }
            composable(Screen.Habits.route) {
                com.rohan.streaky.ui.screens.habits.HabitListScreen(
                    onAddHabit = { navController.navigate(Screen.AddHabit.route) },
                    onHabitClick = { id -> navController.navigate(Screen.HabitDetail.createRoute(id)) }
                )
            }
            composable(Screen.Dashboard.route) {
                com.rohan.streaky.ui.screens.dashboard.DashboardScreen()
            }
            composable(Screen.Settings.route) {
                com.rohan.streaky.ui.screens.settings.SettingsScreen()
            }
            composable(Screen.AddHabit.route) {
                com.rohan.streaky.ui.screens.add.AddHabitScreen(
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(Screen.HabitDetail.route) { back ->
                val id = back.arguments?.getString("habitId")?.toLongOrNull() ?: 0L
                com.rohan.streaky.ui.screens.detail.HabitDetailScreen(
                    habitId = id,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
