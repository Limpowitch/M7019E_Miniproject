package com.example.spotifysonglistapp.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import android.util.Log

class TokenManager(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        "spotify_prefs",
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val TOKEN_KEY = "access_token"
        private const val VERIFIER_KEY = "code_verifier"
    }

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        val token = prefs.getString(TOKEN_KEY, null)
        Log.d("TokenManager", "getToken called, value: $token")
        return token
    }

    fun clearToken() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

    fun saveCodeVerifier(verifier: String) {
        prefs.edit().putString(VERIFIER_KEY, verifier).apply()
    }

    fun getCodeVerifier(): String? = prefs.getString(VERIFIER_KEY, null)

    fun clearCodeVerifier() {
        prefs.edit().remove(VERIFIER_KEY).apply()
    }
}

