package com.example.spotifysonglistapp.models

//This is not accurate (probably) to what Spotify API has, but works for now
data class Song(
    val id: Int,
    val title: String,
    val posterPath: String
)
