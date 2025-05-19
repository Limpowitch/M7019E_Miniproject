// app/src/main/java/com/example/spotifysonglistapp/viewmodel/SongViewModel.kt
package com.example.spotifysonglistapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifysonglistapp.models.ArtistResponse
import com.example.spotifysonglistapp.models.RecentlyPlayedSong
import com.example.spotifysonglistapp.models.Song
import com.example.spotifysonglistapp.models.Track
import com.example.spotifysonglistapp.repository.SpotifyRepository
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SongViewModel(
    private val repository: SpotifyRepository
) : ViewModel() {

    // --- Top‐tracks state ---
    private val _songList = MutableStateFlow<List<Song>>(emptyList())
    val songList: StateFlow<List<Song>> = _songList

    private val _timeRange = MutableStateFlow(TimeRange.SHORT_TERM)
    val timeRange: StateFlow<TimeRange> = _timeRange

    private val _shouldRedirectToLogin = MutableStateFlow(false)
    val shouldRedirectToLogin: StateFlow<Boolean> = _shouldRedirectToLogin

    // --- Selected‐song & artist info state ---
    private val _selectedSong = MutableStateFlow<Song?>(null)
    val selectedSong: StateFlow<Song?> = _selectedSong

    private val _artist = MutableStateFlow<ArtistResponse?>(null)
    val artist: StateFlow<ArtistResponse?> = _artist

    private val _artistTopTracks = MutableStateFlow<List<Track>>(emptyList())
    val artistTopTracks: StateFlow<List<Track>> = _artistTopTracks

    // --- Selected recently-played song state ---
    private val _selectedRecentSong = MutableStateFlow<RecentlyPlayedSong?>(null)
    val selectedRecentSong: StateFlow<RecentlyPlayedSong?> = _selectedRecentSong

    init {
        fetchTopTracks()
    }

    /** Calls Spotify to load your top tracks for the current timeRange */
    fun fetchTopTracks() {
        viewModelScope.launch {
            try {
                val apiValue = timeRange.value.apiValue
                val tracks = repository.getTopTracks(apiValue)
                _songList.value = tracks
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 401) {
                    _shouldRedirectToLogin.value = true
                }
            }
        }
    }

    /** Change the time range and reload top tracks */
    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
        fetchTopTracks()
    }

    /** User tapped on a top track */
    fun selectSong(song: Song) {
        _selectedSong.value = song
        fetchArtistData(song.artistId)
    }

    /** User tapped on a recently-played song */
    fun selectRecentlyPlayedSong(recent: RecentlyPlayedSong) {
        _selectedRecentSong.value = recent
        // Optionally map to Song for details screen
        _selectedSong.value = Song(
            id = recent.id,
            title = recent.title,
            artist = recent.artists,
            artistId = "", // unknown artist ID for recently played
            albumArtUrl = recent.albumArtUrl,
            previewUrl = null,
            spotifyUrl = ""
        )
    }

    /** Fetch artist details + their top tracks */
    fun fetchArtistData(artistId: String) {
        viewModelScope.launch {
            try {
                // 1) Hämta och spara grundinfo om artisten
                val info = repository.getArtistInfo(artistId)
                _artist.value = info

                // 2) Hämta och spara deras topplåtar
                val top = repository.getArtistTopTracks(artistId)
                _artistTopTracks.value = top

                Log.d("SongViewModel", "Artist info: $info")
                Log.d("SongViewModel", "Top tracks: $top")
            } catch (e: Exception) {
                Log.e("SongViewModel", "Error loading artist data", e)
            }
        }
    }
}



