package com.example.spotifysonglistapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifysonglistapp.models.RecentlyPlayedSong
import com.example.spotifysonglistapp.repository.SpotifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RecentlyPlayedViewModel(
    private val repository: SpotifyRepository
) : ViewModel() {

    private val _songs = MutableStateFlow<List<RecentlyPlayedSong>>(emptyList())
    val songs: StateFlow<List<RecentlyPlayedSong>> = _songs

    fun fetchRecentlyPlayed() {
        viewModelScope.launch {
            try {
                val result = repository.getRecentlyPlayed()
                _songs.value = result
            } catch (e: Exception) {
                Log.e("RecentlyPlayedViewModel", "Failed to load recently played songs", e)
                if (e is HttpException && e.code() == 401) {
                    // Optional: add logic to redirect if token is invalid
                }
            }
        }
    }
}
