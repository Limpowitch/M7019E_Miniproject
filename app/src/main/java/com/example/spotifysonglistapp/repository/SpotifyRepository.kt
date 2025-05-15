package com.example.spotifysonglistapp.repository

import android.content.Context
import android.util.Log
import com.example.spotifysonglistapp.auth.TokenManager
import com.example.spotifysonglistapp.models.RecentlyPlayedSong
import com.example.spotifysonglistapp.models.Song
import com.example.spotifysonglistapp.network.SpotifyApiService

class SpotifyRepository(
    private val context: Context,
    private val apiService: SpotifyApiService,
    private val tokenProvider: () -> String? // function to provide token on demand
) {
    suspend fun getTopTracks(timeRange: String): List<Song> {
        val token = tokenProvider() ?: throw IllegalStateException("No access token available")
        try {
            val response = apiService.getTopTracks("Bearer $token", timeRange = timeRange)
            return response.items.map {
                Song(
                    id = it.id,
                    title = it.name,
                    artist = it.artists.joinToString(", ") { artist -> artist.name },
                    albumArtUrl = it.album.images.firstOrNull()?.url ?: ""
                )
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                Log.w("SpotifyRepository", "Token expired or invalid. Clearing and redirecting to login.")
                TokenManager(context).clearToken() // make sure you pass context to repository
                throw e
            } else {
                throw e
            }
        }
    }

    suspend fun getRecentlyPlayed(): List<RecentlyPlayedSong> {
        val token = tokenProvider() ?: throw IllegalStateException("No access token available")
        val response = apiService.getRecentlyPlayed("Bearer $token")
        return response.items.map {
            RecentlyPlayedSong(
                id = it.track.id,
                title = it.track.name,
                artists = it.track.artists.joinToString(", ") { artist -> artist.name },
                albumArtUrl = it.track.album.images.firstOrNull()?.url ?: "",
                playedAt = it.played_at
            )
        }
    }



}

