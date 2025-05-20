package com.example.spotifysonglistapp.models

// Importera din enda Image-klass:
import com.example.spotifysonglistapp.models.Image

data class ArtistResponse(
    val id: String,
    val name: String,
    val followers: Followers,
    val popularity: Int,
    val images: List<Image>      // använder bilden från TopTracksResponse.kt
)

data class Followers(val total: Int)
// Ingen Image-klass här längre!

data class ArtistTopTracksResponse(
    val tracks: List<Track>
)