package com.example.spotifysonglistapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifysonglistapp.models.Song
import com.example.spotifysonglistapp.repository.SpotifyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.example.spotifysonglistapp.auth.TokenManager

class SongViewModel(
    private val repository: SpotifyRepository,
    val tokenManager: TokenManager
) : ViewModel() {

    private val _shouldRedirectToLogin = MutableStateFlow(false)
    val shouldRedirectToLogin: StateFlow<Boolean> = _shouldRedirectToLogin

    private val _songList = MutableStateFlow<List<Song>>(emptyList())
    val songList: StateFlow<List<Song>> = _songList

    private val _selectedSong = MutableStateFlow<Song?>(null)
    val selectedSong: StateFlow<Song?> = _selectedSong

    init {
        viewModelScope.launch {
            try {
                val songs = repository.getTopTracks()
                _songList.value = songs
            } catch (e: Exception) {
                Log.e("SongViewModel", "Failed to load top songs", e)
            }
        }
    }

    fun fetchTopTracks() {
        viewModelScope.launch {
            try {
                val songs = repository.getTopTracks()
                _songList.value = songs
            } catch (e: Exception) {
                Log.e("SongViewModel", "Failed to load top songs", e)
                if (e is HttpException && e.code() == 401) {
                    Log.w("SongViewModel", "401 Unauthorized - token likely expired")
                    tokenManager.clearToken()
                    _shouldRedirectToLogin.value = true
                }
            }

        }
    }

    fun selectSong(song: Song) {
        _selectedSong.value = song
    }
}
