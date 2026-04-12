package com.example.dvartorahapp.data.repository

import com.example.dvartorahapp.data.model.DvarTorah
import kotlinx.coroutines.flow.Flow

interface DvarTorahRepository {
    fun getPublishedDvareiTorah(occasionFilter: String? = null): Flow<List<DvarTorah>>
    fun getDvarTorahById(dvarId: String): Flow<DvarTorah?>
    fun getUserDvareiTorah(authorUid: String): Flow<List<DvarTorah>>
    suspend fun createDvarTorah(dvarTorah: DvarTorah): Result<String>
    suspend fun createAdminPublishedDvarTorah(
        title: String,
        occasion: String,
        authorName: String,
        authorUid: String,
        body: String,
        sources: String
    ): Result<String>
    suspend fun updateDvarTorah(dvarTorah: DvarTorah): Result<Unit>
    suspend fun toggleLike(dvarId: String, uid: String): Result<Unit>
    fun getUserLikedStatus(dvarId: String, uid: String): Flow<Boolean>
    suspend fun updateDvarTorahStatus(dvarId: String, status: String): Result<Unit>
}
