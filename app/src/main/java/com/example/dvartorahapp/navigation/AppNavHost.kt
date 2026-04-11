package com.example.dvartorahapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.example.dvartorahapp.ui.admin.AdminPanelScreen
import com.example.dvartorahapp.ui.apply.WriterApplicationScreen
import com.example.dvartorahapp.ui.auth.AuthState
import com.example.dvartorahapp.ui.auth.AuthViewModel
import com.example.dvartorahapp.ui.auth.LoginScreen
import com.example.dvartorahapp.ui.auth.RegisterScreen
import com.example.dvartorahapp.ui.detail.DvarTorahDetailScreen
import com.example.dvartorahapp.ui.feed.FeedScreen
import com.example.dvartorahapp.ui.profile.ProfileScreen
import com.example.dvartorahapp.ui.profile.ProfileViewModel
import com.example.dvartorahapp.ui.write.WriteScreen

@Composable
fun AppNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userProfile by authViewModel.userProfile.collectAsStateWithLifecycle()

    val currentUser = (authState as? AuthState.Authenticated)?.profile

    data class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)

    val navItems = buildList {
        add(NavItem("Feed", Icons.Filled.Home, Screen.Feed.route))
        if (currentUser?.isWriter == true) add(NavItem("Write", Icons.Outlined.Edit, Screen.WriteCreate.route))
        if (currentUser?.isAdmin == true) add(NavItem("Admin", Icons.Filled.Settings, Screen.AdminPanel.route))
        add(NavItem("Profile", Icons.Filled.Person, Screen.Profile.route))
    }

    Scaffold(
        bottomBar = {
            if (navItems.size > 1) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                NavigationBar {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Feed.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Feed.route) {
                FeedScreen(
                    onDvarClick = { navController.navigate(Screen.DvarDetail.createRoute(it)) }
                )
            }

            composable(Screen.DvarDetail.route) {
                DvarTorahDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    currentUser = currentUser,
                    onRequireAuth = { navController.navigate(Screen.Login.route) }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToFeed = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToFeed = {
                        navController.navigate(Screen.Feed.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.WriteCreate.route) {
                val user = currentUser
                if (user == null || !user.isWriter) {
                    navController.popBackStack()
                } else {
                    WriteScreen(
                        onNavigateBack = { navController.popBackStack() },
                        currentUser = user
                    )
                }
            }

            composable(Screen.WriteEdit.route) {
                val user = currentUser
                if (user == null || !user.isWriter) {
                    navController.popBackStack()
                } else {
                    WriteScreen(
                        onNavigateBack = { navController.popBackStack() },
                        currentUser = user
                    )
                }
            }

            composable(Screen.WriterApply.route) {
                val user = currentUser
                if (user == null) {
                    navController.navigate(Screen.Login.route)
                } else {
                    WriterApplicationScreen(
                        onNavigateBack = { navController.popBackStack() },
                        currentUser = user
                    )
                }
            }

            composable(Screen.Profile.route) {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                val userDvareiTorah by profileViewModel.userDvareiTorah.collectAsStateWithLifecycle()
                val userApplication by profileViewModel.userApplication.collectAsStateWithLifecycle()
                val parshaScheduleMode by profileViewModel.parshaScheduleMode.collectAsStateWithLifecycle()
                ProfileScreen(
                    currentUser = currentUser,
                    userDvareiTorah = userDvareiTorah,
                    userApplication = userApplication,
                    parshaScheduleMode = parshaScheduleMode,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToApply = { navController.navigate(Screen.WriterApply.route) },
                    onNavigateToDvar = { navController.navigate(Screen.DvarDetail.createRoute(it)) },
                    onSignOut = { authViewModel.signOut() },
                    onParshaScheduleModeChange = profileViewModel::setParshaScheduleMode
                )
            }

            composable(Screen.AdminPanel.route) {
                val user = currentUser
                if (user == null || !user.isAdmin) {
                    navController.popBackStack()
                } else {
                    AdminPanelScreen(
                        onNavigateBack = { navController.popBackStack() },
                        currentUser = user
                    )
                }
            }
        }
    }
}
