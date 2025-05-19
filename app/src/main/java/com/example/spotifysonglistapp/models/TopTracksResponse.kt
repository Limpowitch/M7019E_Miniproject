package com.example.spotifysonglistapp.models

data class TopTracksResponse(
    val items: List<Track>
)

data class Track(
    val id: String,
    val name: String,
    val album: Album,
    val artists: List<Artist>,
    val preview_url: String?,
    val external_urls: Map<String, String>
)

data class Album(
    val images: List<Image>
)

data class Artist(
    val id: String,
    val name: String
)

data class Image(
    val url: String
)
