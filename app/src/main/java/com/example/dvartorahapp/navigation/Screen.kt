package com.example.dvartorahapp.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Feed : Screen("feed")
    object DvarDetail : Screen("dvar/{dvarId}") {
        fun createRoute(dvarId: String) = "dvar/$dvarId"
    }
    object WriteCreate : Screen("write")
    object WriteEdit : Screen("write/{dvarId}") {
        fun createRoute(dvarId: String) = "write/$dvarId"
    }
    object WriterApply : Screen("apply")
    object Profile : Screen("profile")
    object AdminPanel : Screen("admin")
    object PrivacyPolicy : Screen("privacy-policy")
    object AccountDeletionPolicy : Screen("account-deletion-policy")
    object ContentPolicy : Screen("content-policy")
}
