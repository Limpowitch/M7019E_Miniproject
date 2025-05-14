package com.example.spotifysonglistapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifysonglistapp.auth.TokenManager
import com.example.spotifysonglistapp.network.SpotifyApiService
import com.example.spotifysonglistapp.repository.SpotifyRepository

class SongViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tokenManager = TokenManager(context)
        val tokenProvider = { tokenManager.getToken() }

        val apiService = SpotifyApiService.create(tokenProvider)
        val repository = SpotifyRepository(context, apiService, tokenProvider)

        @Suppress("UNCHECKED_CAST")
        return SongViewModel(repository, tokenManager) as T
    }
}

