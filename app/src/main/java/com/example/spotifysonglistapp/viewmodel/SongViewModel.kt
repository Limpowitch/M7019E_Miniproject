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
import com.example.spotifysonglistapp.models.ArtistResponse
import com.example.spotifysonglistapp.models.RecentlyPlayedSong
import com.example.spotifysonglistapp.models.Track

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

    private val _artist = MutableStateFlow<ArtistResponse?>(null)
    val artist: StateFlow<ArtistResponse?> = _artist

    private val _artistTopTracks = MutableStateFlow<List<Track>>(emptyList())
    val artistTopTracks: StateFlow<List<Track>> = _artistTopTracks

    init {
        fetchTopTracks()
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

    fun fetchArtistData(artistId: String) {
        viewModelScope.launch {
            try {
                // 1) Hämta och spara grundinfo om artisten
                val info = repository.getArtistInfo(artistId)
                _artist.value = info

                // 2) Hämta och spara deras topplåtar
                val top = repository.getArtistTopTracks(artistId)
                _artistTopTracks.value = top

                Log.d("ArtistData", "Artist info: $info")
                Log.d("ArtistData", "Top tracks: $top")
            } catch (e: Exception) {
                Log.e("ArtistData", "Error loading artist data", e)
            }
        }
    }

}
