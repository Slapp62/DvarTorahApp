package com.quickdvartorah.app.data.repository

import android.app.Activity
import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.FirebaseNetworkException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
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
    override fun isGoogleSignInConfigured(): Boolean = appContext.getGoogleWebClientIdOrNull() != null

    override fun authStateFlow(): Flow<FirebaseUser?> = callbackFlow {
        trySend(auth.currentUser)
        val listener = FirebaseAuth.AuthStateListener { trySend(it.currentUser) }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(email: String, password: String): Result<FirebaseUser> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await().user!!
    }

    override suspend fun signInWithGoogle(activity: Activity): Result<FirebaseUser> = runCatching {
        val webClientId = appContext.getGoogleWebClientId()
        val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        val result = try {
            credentialManager.getCredential(activity, request)
        } catch (error: NoCredentialException) {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            credentialManager.getCredential(activity, request)
        }
        val credential = result.credential
        val googleIdTokenCredential = when {
            credential is CustomCredential &&
                credential.type.isGoogleIdTokenCredentialType() -> {
                try {
                    GoogleIdTokenCredential.createFrom(credential.data)
                } catch (exception: GoogleIdTokenParsingException) {
                    throw IllegalStateException("Google sign-in response could not be read.", exception)
                }
            }

            else -> throw IllegalStateException("Google sign-in returned an unsupported credential type.")
        }

        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
        auth.signInWithCredential(firebaseCredential).await().user
            ?: error("Google sign-in did not return a Firebase user.")
    }.mapFailure(::toUserFacingGoogleSignInError)

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

private fun <T> Result<T>.mapFailure(transform: (Throwable) -> Throwable): Result<T> =
    fold(
        onSuccess = { Result.success(it) },
        onFailure = { Result.failure(transform(it)) }
    )

private fun Context.getGoogleWebClientId(): String {
    return getGoogleWebClientIdOrNull() ?: error(
        "Google sign-in is not fully configured. Add a Web OAuth client in Firebase and refresh google-services.json."
    )
}

private fun Context.getGoogleWebClientIdOrNull(): String? {
    val resId = resources.getIdentifier("default_web_client_id", "string", packageName)
    return if (resId == 0) null else getString(resId)
}

private fun toUserFacingGoogleSignInError(error: Throwable): Throwable {
    val message = when (error) {
        is GetCredentialCancellationException -> "Google sign-in was cancelled."
        is NoCredentialException -> "No Google account could be used on this device. Make sure a Google account is added and Google Play Services is up to date."
        is FirebaseNetworkException -> "Could not connect. Check your internet connection."
        is FirebaseAuthInvalidCredentialsException -> "Google sign-in could not be verified. Check Firebase Google Auth config (package, SHA-1/SHA-256, and google-services.json)."
        is GoogleIdTokenParsingException -> "Google sign-in response could not be read."
        is GetCredentialException -> error.message ?: "Google sign-in is unavailable right now."
        else -> error.message ?: "Could not sign in with Google"
    }
    return IllegalStateException(message, error)
}

private fun String.isGoogleIdTokenCredentialType(): Boolean {
    return this == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL ||
        this == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_SIWG_CREDENTIAL
}
