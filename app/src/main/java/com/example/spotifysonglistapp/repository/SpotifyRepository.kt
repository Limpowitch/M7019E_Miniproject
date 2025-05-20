package com.example.spotifysonglistapp.repository

import android.content.Context
import android.util.Log
import com.example.spotifysonglistapp.auth.TokenManager
import com.example.spotifysonglistapp.models.ArtistResponse
import com.example.spotifysonglistapp.models.ArtistTopTracksResponse
import com.example.spotifysonglistapp.models.RecentlyPlayedSong
import com.example.spotifysonglistapp.models.Song
import com.example.spotifysonglistapp.models.Track
import com.example.spotifysonglistapp.network.SpotifyApiService
import retrofit2.HttpException

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
                Log.d("PreviewCheck", "Track: ${it.name}, preview_url: ${it.preview_url}")
                Song(
                    id = it.id,
                    title = it.name,
                    artist = it.artists.joinToString(", ") { artist -> artist.name },
                    albumArtUrl = it.album.images.firstOrNull()?.url ?: "",
                    previewUrl = it.preview_url,
                    spotifyUrl = it.external_urls["spotify"] ?: "",
                    artistId = it.artists.firstOrNull()?.id ?: ""
                )
            }
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 401) {
                Log.w("SpotifyRepository", "Token expired or invalid. Clearing and redirecting to login.")
                TokenManager(context).clearToken() // super important to pass context (dependency injection all the way)
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
                playedAt = it.played_at,
                spotifyUrl = it.track.external_urls["spotify"] ?: "",
                artistId = it.track.artists.firstOrNull()?.id ?: ""
            )
        }
    }

    suspend fun getArtistInfo(artistId: String): ArtistResponse {
        return apiService.getArtist(artistId)
    }

    suspend fun getArtistTopTracks(artistId: String): List<Track> {
        return try {
            val token = tokenProvider() ?: throw IllegalStateException("No access token available")
            val response = apiService.getArtistTopTracks("Bearer $token", artistId, "SE")
            response.tracks
        } catch (e: HttpException) {
            Log.e("SpotifyRepository", "Failed to fetch top tracks for $artistId", e)
            throw e
        }
    }



}

