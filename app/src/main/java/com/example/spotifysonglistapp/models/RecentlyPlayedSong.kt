package com.example.spotifysonglistapp.models

data class RecentlyPlayedSong(
    val id: String,
    val title: String,
    val artists: String,
    val albumArtUrl: String,
    val playedAt: String,
    val previewUrl: String? = null,
    val spotifyUrl: String,
    val artistId: String
)

