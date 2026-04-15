package com.quickdvartorah.app.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quickdvartorah.app.ui.admin.AdminPanelScreen
import com.quickdvartorah.app.ui.apply.WriterApplicationScreen
import com.quickdvartorah.app.ui.auth.AuthState
import com.quickdvartorah.app.ui.auth.AuthViewModel
import com.quickdvartorah.app.ui.auth.LoginScreen
import com.quickdvartorah.app.ui.auth.RegisterScreen
import com.quickdvartorah.app.ui.browse.BrowseScreen
import com.quickdvartorah.app.ui.detail.DvarTorahDetailScreen
import com.quickdvartorah.app.ui.feed.FeedScreen
import com.quickdvartorah.app.ui.policy.PolicyContent
import com.quickdvartorah.app.ui.policy.PolicyScreen
import com.quickdvartorah.app.ui.profile.ProfileScreen
import com.quickdvartorah.app.ui.profile.ProfileViewModel
import com.quickdvartorah.app.ui.saved.SavedScreen
import com.quickdvartorah.app.ui.write.WriteScreen

@Composable
fun AppNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val userDvareiTorah by profileViewModel.userDvareiTorah.collectAsStateWithLifecycle()
    val userApplication by profileViewModel.userApplication.collectAsStateWithLifecycle()
    val parshaScheduleMode by profileViewModel.parshaScheduleMode.collectAsStateWithLifecycle()
    val isDeletingAccount by profileViewModel.isDeletingAccount.collectAsStateWithLifecycle()
    val appSnackbarHostState = remember { SnackbarHostState() }

    val currentUser = (authState as? AuthState.Authenticated)?.profile

    data class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)

    val navItems = buildList {
        add(NavItem("Home", Icons.Filled.Home, Screen.Feed.route))
        add(NavItem("Browse", Icons.Outlined.Explore, Screen.Browse.route))
        add(NavItem("Saved", Icons.Outlined.BookmarkBorder, Screen.Saved.route))
        if (currentUser?.hasWriterAccess == true) add(NavItem("Write", Icons.Outlined.Edit, Screen.WriteCreate.route))
        if (currentUser?.hasAdminAccess == true) add(NavItem("Admin", Icons.Filled.Settings, Screen.AdminPanel.route))
        add(NavItem("Profile", Icons.Filled.Person, Screen.Profile.route))
    }

    LaunchedEffect(profileViewModel) {
        profileViewModel.effect.collect { effect ->
            when (effect) {
                is com.quickdvartorah.app.ui.profile.ProfileUiEffect.AccountDeleted -> {
                    appSnackbarHostState.showSnackbar("Your account was deleted.")
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = false }
                        launchSingleTop = true
                    }
                }

                is com.quickdvartorah.app.ui.profile.ProfileUiEffect.ShowMessage -> {
                    appSnackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(appSnackbarHostState) },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val showBottomBar = currentDestination?.hierarchy?.any { destination ->
                navItems.any { it.route == destination.route }
            } == true

            if (navItems.size > 1 && showBottomBar) {
                Surface(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.9f),
                    tonalElevation = 0.dp,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                        shadowElevation = 6.dp
                    ) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.0f),
                            tonalElevation = 0.dp,
                            windowInsets = WindowInsets(0, 0, 0, 0)
                        ) {
                            navItems.forEach { item ->
                                NavigationBarItem(
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = { Text(item.label) },
                                    selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
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

            composable(Screen.Browse.route) {
                BrowseScreen(
                    onDvarClick = { navController.navigate(Screen.DvarDetail.createRoute(it)) }
                )
            }

            composable(Screen.Saved.route) {
                SavedScreen(
                    onDvarClick = { navController.navigate(Screen.DvarDetail.createRoute(it)) },
                    onRequireAuth = { navController.navigate(Screen.Login.route) }
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
                if (user == null || !user.hasWriterAccess) {
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
                if (user == null || !user.hasWriterAccess) {
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
                        onNavigateToContentPolicy = { navController.navigate(Screen.ContentPolicy.route) },
                        currentUser = user
                    )
                }
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    currentUser = currentUser,
                    userDvareiTorah = userDvareiTorah,
                    userApplication = userApplication,
                    parshaScheduleMode = parshaScheduleMode,
                    showManageAdPrivacy = false,
                    isDeletingAccount = isDeletingAccount,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToApply = { navController.navigate(Screen.WriterApply.route) },
                    onNavigateToWrite = { navController.navigate(Screen.WriteCreate.route) },
                    onNavigateToDvar = { navController.navigate(Screen.DvarDetail.createRoute(it)) },
                    onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onNavigateToAccountDeletionPolicy = { navController.navigate(Screen.AccountDeletionPolicy.route) },
                    onNavigateToContentPolicy = { navController.navigate(Screen.ContentPolicy.route) },
                    onSignOut = { authViewModel.signOut() },
                    onParshaScheduleModeChange = profileViewModel::setParshaScheduleMode,
                    onManageAdPrivacy = { },
                    onDeleteAccount = profileViewModel::deleteAccount
                )
            }

            composable(Screen.PrivacyPolicy.route) {
                PolicyScreen(
                    title = "Privacy Policy",
                    intro = "This policy explains what information Quick Dvar Torah collects, how it is used, and what choices users have.",
                    sections = PolicyContent.privacySections,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AccountDeletionPolicy.route) {
                PolicyScreen(
                    title = "Account Deletion",
                    intro = "This page explains how account deletion works in Quick Dvar Torah and what data is removed.",
                    sections = PolicyContent.accountDeletionSections,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ContentPolicy.route) {
                PolicyScreen(
                    title = "Content Policy",
                    intro = "Quick Dvar Torah is intended for thoughtful Torah learning. This policy explains what belongs in the app and what does not.",
                    sections = PolicyContent.contentPolicySections,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AdminPanel.route) {
                val user = currentUser
                if (user == null || !user.hasAdminAccess) {
                    navController.popBackStack()
                } else {
                    AdminPanelScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onOpenDvar = { navController.navigate(Screen.DvarDetail.createRoute(it)) },
                        currentUser = user
                    )
                }
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
