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
import com.example.spotifysonglistapp.models.RecentlyPlayedSong

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

    private val _timeRange = MutableStateFlow(TimeRange.SHORT_TERM)
    val timeRange: StateFlow<TimeRange> = _timeRange

    private val _selectedRecentSong = MutableStateFlow<RecentlyPlayedSong?>(null)
    val selectedRecentSong: StateFlow<RecentlyPlayedSong?> = _selectedRecentSong



    init {
        fetchTopTracks()
    }

    fun selectRecentlyPlayedSong(song: RecentlyPlayedSong) {
        _selectedRecentSong.value = song
    }

    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
        fetchTopTracks()
    }

    fun fetchTopTracks() {
        viewModelScope.launch {
            try {
                val result = repository.getTopTracks(timeRange.value.apiValue)
                _songList.value = result
            } catch (e: Exception) {
                Log.e("SongViewModel", "Failed to load top songs", e)
                if (e is HttpException && e.code() == 401) {
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
