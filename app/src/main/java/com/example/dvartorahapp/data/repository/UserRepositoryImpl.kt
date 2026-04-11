package com.example.dvartorahapp.data.repository

import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    private val collection get() = firestore.collection(FirestoreConstants.COLLECTION_USERS)

    override fun getUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val listener = collection.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(kotlinx.coroutines.CancellationException(error.message ?: "Firestore error", error))
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(UserProfile::class.java))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createUserProfile(profile: UserProfile): Result<Unit> = runCatching {
        collection.document(profile.uid).set(profile).await()
    }

    override suspend fun updateUserRole(uid: String, role: String): Result<Unit> = runCatching {
        collection.document(uid).update(FirestoreConstants.UserFields.ROLE, role).await()
    }

    override suspend fun updateProfileImageUrl(uid: String, imageUrl: String): Result<Unit> = runCatching {
        collection.document(uid).update(FirestoreConstants.UserFields.PROFILE_IMAGE_URL, imageUrl).await()
    }
}
