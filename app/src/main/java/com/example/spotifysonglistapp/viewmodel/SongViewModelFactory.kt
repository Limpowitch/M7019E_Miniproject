// app/src/main/java/com/example/spotifysonglistapp/viewmodel/SongViewModelFactory.kt
package com.example.spotifysonglistapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifysonglistapp.network.SpotifyApiService
import com.example.spotifysonglistapp.repository.SpotifyRepository

/**
 * Factory for creating [SongViewModel] instances.
 */
class SongViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Provide tokenProvider lambda
        val tokenManager = com.example.spotifysonglistapp.auth.TokenManager(context)
        val tokenProvider = { tokenManager.getToken() }

        // Create API service and repository
        val apiService = SpotifyApiService.create(tokenProvider)
        val repository = SpotifyRepository(context, apiService, tokenProvider)

        // Instantiate ViewModel with repository only
        @Suppress("UNCHECKED_CAST")
        return SongViewModel(repository) as T
    }
}
