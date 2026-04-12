package com.example.dvartorahapp.data.model

import com.example.dvartorahapp.data.remote.FirestoreConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserProfileTest {
    @Test
    fun `admin flag elevates effective role to admin`() {
        val profile = UserProfile(role = FirestoreConstants.Roles.VIEWER, admin = true)

        assertEquals(FirestoreConstants.Roles.ADMIN, profile.effectiveRole)
        assertTrue(profile.isAdmin)
        assertTrue(profile.isWriter)
        assertFalse(profile.isViewer)
    }

    @Test
    fun `writer role grants writer access without admin access`() {
        val profile = UserProfile(role = FirestoreConstants.Roles.WRITER)

        assertEquals(FirestoreConstants.Roles.WRITER, profile.effectiveRole)
        assertTrue(profile.isWriter)
        assertFalse(profile.isAdmin)
        assertFalse(profile.isViewer)
    }
}
