package com.example.dvartorahapp.data.repository

import com.example.dvartorahapp.data.model.UserProfile
import com.example.dvartorahapp.data.remote.FirestoreConstants
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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
    private val applications get() = firestore.collection(FirestoreConstants.COLLECTION_WRITER_APPLICATIONS)
    private val dvarim get() = firestore.collection(FirestoreConstants.COLLECTION_DIVREI_TORAH)
    private val reports get() = firestore.collection(FirestoreConstants.COLLECTION_REPORTS)

    override fun getUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val listener = collection.document(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(UserProfile::class.java))
        }
        awaitClose { listener.remove() }
    }

    override suspend fun createUserProfile(profile: UserProfile): Result<Unit> = runCatching {
        collection.document(profile.uid).set(profile).await()
    }

    override suspend fun ensureUserProfile(uid: String, displayName: String, email: String): Result<Unit> = runCatching {
        val existing = collection.document(uid).get().await()
        if (!existing.exists()) {
            collection.document(uid).set(
                UserProfile(
                    uid = uid,
                    displayName = displayName,
                    email = email,
                    role = FirestoreConstants.Roles.VIEWER
                )
            ).await()
            return@runCatching
        }

        val patch = buildMap<String, Any> {
            if ((existing.getString(FirestoreConstants.UserFields.DISPLAY_NAME).orEmpty().isBlank()) && displayName.isNotBlank()) {
                put(FirestoreConstants.UserFields.DISPLAY_NAME, displayName)
            }
            if ((existing.getString(FirestoreConstants.UserFields.EMAIL).orEmpty().isBlank()) && email.isNotBlank()) {
                put(FirestoreConstants.UserFields.EMAIL, email)
            }
        }

        if (patch.isNotEmpty()) {
            collection.document(uid).set(patch, SetOptions.merge()).await()
        }
    }

    override suspend fun updateUserRole(uid: String, role: String): Result<Unit> = runCatching {
        collection.document(uid).update(FirestoreConstants.UserFields.ROLE, role).await()
    }

    override suspend fun updateProfileImageUrl(uid: String, imageUrl: String): Result<Unit> = runCatching {
        collection.document(uid).update(FirestoreConstants.UserFields.PROFILE_IMAGE_URL, imageUrl).await()
    }

    override suspend fun deleteAccountData(uid: String): Result<Unit> = runCatching {
        val documentRefs = linkedSetOf<DocumentReference>()

        documentRefs += collection.document(uid)
        documentRefs += applications.document(uid)

        val authoredDvarDocs = dvarim
            .whereEqualTo(FirestoreConstants.DvarTorahFields.AUTHOR_UID, uid)
            .get()
            .await()
            .documents

        authoredDvarDocs.forEach { dvarDoc ->
            documentRefs += dvarDoc.reference

            dvarDoc.reference.collection(FirestoreConstants.COLLECTION_LIKES)
                .get()
                .await()
                .documents
                .forEach { likeDoc -> documentRefs += likeDoc.reference }

            reports
                .whereEqualTo(FirestoreConstants.ReportFields.DVAR_ID, dvarDoc.id)
                .get()
                .await()
                .documents
                .forEach { reportDoc -> documentRefs += reportDoc.reference }
        }

        firestore.collectionGroup(FirestoreConstants.COLLECTION_LIKES)
            .whereEqualTo("uid", uid)
            .get()
            .await()
            .documents
            .forEach { likeDoc -> documentRefs += likeDoc.reference }

        reports
            .whereEqualTo(FirestoreConstants.ReportFields.REPORTER_UID, uid)
            .get()
            .await()
            .documents
            .forEach { reportDoc -> documentRefs += reportDoc.reference }

        documentRefs.chunked(400).forEach { refs ->
            firestore.runBatch { batch ->
                refs.forEach(batch::delete)
            }.await()
        }
    }
}
