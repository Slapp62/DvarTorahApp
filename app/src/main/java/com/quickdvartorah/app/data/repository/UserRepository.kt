package com.quickdvartorah.app.data.repository

import com.quickdvartorah.app.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserProfile(uid: String): Flow<UserProfile?>
    suspend fun createUserProfile(profile: UserProfile): Result<Unit>
    suspend fun ensureUserProfile(uid: String, displayName: String, email: String): Result<Unit>
    suspend fun updateUserRole(uid: String, role: String): Result<Unit>
    suspend fun updateProfileImageUrl(uid: String, imageUrl: String): Result<Unit>
    suspend fun deleteAccountData(uid: String): Result<Unit>
}
