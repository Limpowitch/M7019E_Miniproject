package com.example.spotifysonglistapp.models

import com.example.spotifysonglistapp.models.Image

data class ArtistResponse(
    val id: String,
    val name: String,
    val followers: Followers,
    val popularity: Int,
    val images: List<Image>
)

data class Followers(val total: Int)

data class ArtistTopTracksResponse(
    val tracks: List<Track>
)