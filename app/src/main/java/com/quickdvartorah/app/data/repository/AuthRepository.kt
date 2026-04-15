package com.quickdvartorah.app.data.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun isGoogleSignInConfigured(): Boolean
    fun authStateFlow(): Flow<FirebaseUser?>
    suspend fun signIn(email: String, password: String): Result<FirebaseUser>
    suspend fun signInWithGoogle(activity: Activity): Result<FirebaseUser>
    suspend fun register(email: String, password: String): Result<FirebaseUser>
    suspend fun deleteCurrentUser(): Result<Unit>
    suspend fun signOut()
}
