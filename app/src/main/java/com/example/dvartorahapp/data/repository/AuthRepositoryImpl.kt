package com.example.dvartorahapp.data.repository

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    @param:ApplicationContext private val appContext: Context
) : AuthRepository {

    private val credentialManager by lazy { CredentialManager.create(appContext) }

    override val currentUser: FirebaseUser? get() = auth.currentUser

    override fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await().user!!
    }

    override suspend fun signInWithGoogle(activity: Activity): Result<FirebaseUser> = runCatching {
        val webClientId = appContext.getGoogleWebClientId()
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(webClientId)
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential
        val googleIdTokenCredential = when {
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                try {
                    GoogleIdTokenCredential.createFrom(credential.data)
                } catch (exception: GoogleIdTokenParsingException) {
                    throw IllegalStateException("Google sign-in response could not be read.", exception)
                }
            }

            else -> throw IllegalStateException("Google sign-in was cancelled.")
        }

        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        auth.signInWithCredential(firebaseCredential).await().user
            ?: error("Google sign-in did not return a Firebase user.")
    }

    override suspend fun register(email: String, password: String): Result<FirebaseUser> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await().user!!
    }

    override suspend fun deleteCurrentUser(): Result<Unit> = runCatching {
        auth.currentUser?.delete()?.await() ?: error("No signed-in account to delete.")
    }

    override suspend fun signOut() {
        auth.signOut()
        runCatching {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }
    }
}

private fun Context.getGoogleWebClientId(): String {
    val resId = resources.getIdentifier("default_web_client_id", "string", packageName)
    require(resId != 0) {
        "Google sign-in is not fully configured. Add a Web OAuth client in Firebase and refresh google-services.json."
    }
    return getString(resId)
}
