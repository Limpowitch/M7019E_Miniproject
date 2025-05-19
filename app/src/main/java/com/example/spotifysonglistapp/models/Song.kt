package com.example.spotifysonglistapp.models

data class Song(
    val id: String,
    val title: String,
    val artist: String,
    val artistId: String,    // the Spotify artist ID
    val albumArtUrl: String,
    val previewUrl: String? = null,
    val spotifyUrl: String
)
