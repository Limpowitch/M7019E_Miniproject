package com.example.spotifysonglistapp.models

data class RecentlyPlayedResponse(
    val items: List<RecentlyPlayedItem>
)

data class RecentlyPlayedItem(
    val track: Track,
    val played_at: String
)
