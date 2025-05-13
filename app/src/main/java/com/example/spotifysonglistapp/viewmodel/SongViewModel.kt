package com.example.spotifysonglistapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spotifysonglistapp.models.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SongViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _songList = MutableStateFlow<List<Song?>>(emptyList())
    val         songList: StateFlow<List<Song?>> = _songList

    private val _selectedSong = MutableStateFlow<Song?>(null)
    val         selectedSong: StateFlow<Song?> = _selectedSong

    init {
        viewModelScope.launch {
            // Below needs to be uncommented once API is setup
            //_songList.value = repo.fetchSongList()


            val sampleSong = Song( // Just for testing
                id = 0,
                title = "Test Song",
                posterPath = ""
            )
            _songList.value = listOf(sampleSong) // we have this here until API is fixed
        }
    }

    fun selectSong(song: Song?) {
        _selectedSong.value = song
    }

}