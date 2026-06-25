package com.rohan.streaky.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*

sealed class Screen(val route: String) {
    data object Splash      : Screen("splash")
    data object Today       : Screen("today")
    data object Habits      : Screen("habits")
    data object Dashboard   : Screen("dashboard")
    data object Settings    : Screen("settings")
    data object AddHabit    : Screen("add_habit")
    data object HabitDetail : Screen("habit_detail/{habitId}") {
        fun createRoute(id: Long) = "habit_detail/$id"
    }
    data object EditHabit   : Screen("edit_habit/{habitId}") {
        fun createRoute(id: Long) = "edit_habit/$id"
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val TAB_ROUTES = listOf(
    Screen.Today.route,
    Screen.Habits.route,
    Screen.Dashboard.route,
    Screen.Settings.route
)

private fun NavBackStackEntry.isTab() = destination.route in TAB_ROUTES

// Smooth, natural easing spec used for all transitions
private val smoothSpec = tween<Float>(durationMillis = 200, easing = FastOutSlowInEasing)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStack  by navController.currentBackStackEntryAsState()
    val currentRoute  = navBackStack?.destination?.route

    val bottomNavItems = listOf(
        NavItem(Screen.Today.route,     "Today",     Icons.Filled.Home,     Icons.Outlined.Home),
        NavItem(Screen.Habits.route,    "Habits",    Icons.Filled.CheckBox, Icons.Outlined.CheckBoxOutlineBlank),
        NavItem(Screen.Dashboard.route, "Dashboard", Icons.Filled.BarChart, Icons.Outlined.BarChart),
        NavItem(Screen.Settings.route,  "Settings",  Icons.Filled.Settings, Icons.Outlined.Settings),
    )

    val showBottomBar = currentRoute in TAB_ROUTES

    var swipeDeltaX by remember { mutableFloatStateOf(0f) }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter   = fadeIn(tween(180)) + slideInVertically(tween(180)) { it },
                exit    = fadeOut(tween(180)) + slideOutVertically(tween(180)) { it }
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = androidx.compose.ui.unit.Dp(0f)
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick  = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            },
                            icon  = {
                                Icon(
                                    if (selected) item.selectedIcon else item.unselectedIcon,
                                    item.label
                                )
                            },
                            label  = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
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
            navController    = navController,
            startDestination = Screen.Splash.route,
            modifier         = Modifier
                .padding(padding)
                .pointerInput(currentRoute) {
                    if (currentRoute in TAB_ROUTES) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                val idx = TAB_ROUTES.indexOf(currentRoute)
                                when {
                                    swipeDeltaX > 80f && idx > 0 -> {
                                        navController.navigate(TAB_ROUTES[idx - 1]) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState    = true
                                        }
                                    }
                                    swipeDeltaX < -80f && idx < TAB_ROUTES.size - 1 -> {
                                        navController.navigate(TAB_ROUTES[idx + 1]) {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState    = true
                                        }
                                    }
                                }
                                swipeDeltaX = 0f
                            },
                            onDragCancel     = { swipeDeltaX = 0f },
                            onHorizontalDrag = { _, delta -> swipeDeltaX += delta }
                        )
                    }
                },
            // Tab switches: pure crossfade — zero jank on any device
            // Screen push/pop: short subtle slide from edge sixth + fade
            enterTransition = {
                if (initialState.isTab() && targetState.isTab()) {
                    fadeIn(smoothSpec)
                } else {
                    fadeIn(smoothSpec) + slideInHorizontally(tween(220, easing = FastOutSlowInEasing)) { it / 6 }
                }
            },
            exitTransition = {
                if (initialState.isTab() && targetState.isTab()) {
                    fadeOut(smoothSpec)
                } else {
                    fadeOut(smoothSpec) + slideOutHorizontally(tween(220, easing = FastOutSlowInEasing)) { -it / 6 }
                }
            },
            popEnterTransition  = {
                fadeIn(smoothSpec) + slideInHorizontally(tween(220, easing = FastOutSlowInEasing)) { -it / 6 }
            },
            popExitTransition   = {
                fadeOut(smoothSpec) + slideOutHorizontally(tween(220, easing = FastOutSlowInEasing)) { it / 6 }
            }
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
                    onAddHabit   = { navController.navigate(Screen.AddHabit.route) },
                    onHabitClick = { id -> navController.navigate(Screen.HabitDetail.createRoute(id)) }
                )
            }
            composable(Screen.Habits.route) {
                com.rohan.streaky.ui.screens.habits.HabitListScreen(
                    onAddHabit   = { navController.navigate(Screen.AddHabit.route) },
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
                    onBack  = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
            composable(Screen.HabitDetail.route) { back ->
                val id = back.arguments?.getString("habitId")?.toLongOrNull() ?: 0L
                com.rohan.streaky.ui.screens.detail.HabitDetailScreen(
                    habitId = id,
                    onBack  = { navController.popBackStack() },
                    onEdit  = { navController.navigate(Screen.EditHabit.createRoute(id)) }
                )
            }
            composable(Screen.EditHabit.route) { back ->
                val id = back.arguments?.getString("habitId")?.toLongOrNull() ?: 0L
                com.rohan.streaky.ui.screens.edit.EditHabitScreen(
                    habitId = id,
                    onBack  = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }
        }
    }
}
