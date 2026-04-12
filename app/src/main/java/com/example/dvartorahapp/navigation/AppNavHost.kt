package com.example.dvartorahapp.navigation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.dvartorahapp.ads.AdsViewModel
import com.example.dvartorahapp.ui.admin.AdminPanelScreen
import com.example.dvartorahapp.ui.apply.WriterApplicationScreen
import com.example.dvartorahapp.ui.auth.AuthState
import com.example.dvartorahapp.ui.auth.AuthViewModel
import com.example.dvartorahapp.ui.auth.LoginScreen
import com.example.dvartorahapp.ui.auth.RegisterScreen
import com.example.dvartorahapp.ui.detail.DvarTorahDetailScreen
import com.example.dvartorahapp.ui.feed.FeedScreen
import com.example.dvartorahapp.ui.components.BannerAdBar
import com.example.dvartorahapp.ui.policy.PolicyContent
import com.example.dvartorahapp.ui.policy.PolicyScreen
import com.example.dvartorahapp.ui.profile.ProfileScreen
import com.example.dvartorahapp.ui.profile.ProfileViewModel
import com.example.dvartorahapp.ui.write.WriteScreen

@Composable
fun AppNavHost(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val adsViewModel: AdsViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val adsUiState by adsViewModel.uiState.collectAsStateWithLifecycle()
    val userDvareiTorah by profileViewModel.userDvareiTorah.collectAsStateWithLifecycle()
    val userApplication by profileViewModel.userApplication.collectAsStateWithLifecycle()
    val parshaScheduleMode by profileViewModel.parshaScheduleMode.collectAsStateWithLifecycle()
    val isDeletingAccount by profileViewModel.isDeletingAccount.collectAsStateWithLifecycle()
    val appSnackbarHostState = remember { SnackbarHostState() }
    val activity = LocalContext.current.findActivity()

    val currentUser = (authState as? AuthState.Authenticated)?.profile

    data class NavItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)

    val navItems = buildList {
        add(NavItem("Feed", Icons.Filled.Home, Screen.Feed.route))
        if (currentUser?.hasWriterAccess == true) add(NavItem("Write", Icons.Outlined.Edit, Screen.WriteCreate.route))
        if (currentUser?.hasAdminAccess == true) add(NavItem("Admin", Icons.Filled.Settings, Screen.AdminPanel.route))
        add(NavItem("Profile", Icons.Filled.Person, Screen.Profile.route))
    }

    LaunchedEffect(activity) {
        activity?.let(adsViewModel::refreshConsent)
    }

    LaunchedEffect(profileViewModel) {
        profileViewModel.effect.collect { effect ->
            when (effect) {
                is com.example.dvartorahapp.ui.profile.ProfileUiEffect.AccountDeleted -> {
                    appSnackbarHostState.showSnackbar("Your account was deleted.")
                    navController.navigate(Screen.Feed.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = false }
                        launchSingleTop = true
                    }
                }

                is com.example.dvartorahapp.ui.profile.ProfileUiEffect.ShowMessage -> {
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
                androidx.compose.foundation.layout.Column {
                    if (adsUiState.canRequestAds) {
                        BannerAdBar()
                    }
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
                    showManageAdPrivacy = adsUiState.privacyOptionsRequired,
                    isDeletingAccount = isDeletingAccount,
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                    onNavigateToApply = { navController.navigate(Screen.WriterApply.route) },
                    onNavigateToDvar = { navController.navigate(Screen.DvarDetail.createRoute(it)) },
                    onNavigateToPrivacyPolicy = { navController.navigate(Screen.PrivacyPolicy.route) },
                    onNavigateToAccountDeletionPolicy = { navController.navigate(Screen.AccountDeletionPolicy.route) },
                    onNavigateToContentPolicy = { navController.navigate(Screen.ContentPolicy.route) },
                    onSignOut = { authViewModel.signOut() },
                    onParshaScheduleModeChange = profileViewModel::setParshaScheduleMode,
                    onManageAdPrivacy = {
                        activity?.let {
                            adsViewModel.showPrivacyOptions(it) { errorMessage ->
                                if (!errorMessage.isNullOrBlank()) {
                                    profileViewModel.showMessage(errorMessage)
                                }
                            }
                        }
                    },
                    onDeleteAccount = profileViewModel::deleteAccount
                )
            }

            composable(Screen.PrivacyPolicy.route) {
                PolicyScreen(
                    title = "Privacy Policy",
                    intro = "This policy explains what information ShabbosVorts collects, how it is used, and what choices users have.",
                    sections = PolicyContent.privacySections,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.AccountDeletionPolicy.route) {
                PolicyScreen(
                    title = "Account Deletion",
                    intro = "This page explains how account deletion works in ShabbosVorts and what data is removed.",
                    sections = PolicyContent.accountDeletionSections,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.ContentPolicy.route) {
                PolicyScreen(
                    title = "Content Policy",
                    intro = "ShabbosVorts is intended for thoughtful Torah learning. This policy explains what belongs in the app and what does not.",
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
