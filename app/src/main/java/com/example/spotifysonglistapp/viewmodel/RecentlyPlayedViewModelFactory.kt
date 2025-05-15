package com.example.spotifysonglistapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spotifysonglistapp.repository.SpotifyRepository

class RecentlyPlayedViewModelFactory(
    private val repository: SpotifyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecentlyPlayedViewModel(repository) as T
    }
}
