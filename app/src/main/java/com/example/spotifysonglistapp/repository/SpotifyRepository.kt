// app/src/main/java/com/example/spotifysonglistapp/repository/SpotifyRepository.kt
package com.example.spotifysonglistapp.repository

import android.content.Context
import android.util.Log
import com.example.spotifysonglistapp.auth.TokenManager
import com.example.spotifysonglistapp.models.ArtistResponse
import com.example.spotifysonglistapp.models.RecentlyPlayedSong
import com.example.spotifysonglistapp.models.Song
import com.example.spotifysonglistapp.models.Track
import com.example.spotifysonglistapp.models.TopTracksResponse
import com.example.spotifysonglistapp.network.SpotifyApiService
import retrofit2.HttpException

class SpotifyRepository(
    private val context: Context,
    private val apiService: SpotifyApiService,
    private val tokenProvider: () -> String?
) {
    suspend fun getTopTracks(timeRange: String): List<Song> {
        val token = tokenProvider()
            ?: throw IllegalStateException("No access token available")
        return try {
            val response: TopTracksResponse =
                apiService.getTopTracks("Bearer $token", timeRange = timeRange)

            response.items.map { t: Track ->
                // pick first artist to drive artistId
                val primaryArtist = t.artists.firstOrNull()
                    ?: throw IllegalStateException("No artist on track ${t.id}")
                Log.d("PreviewCheck", "Track: ${t.name}, preview_url: ${t.preview_url}")

                Song(
                    id           = t.id,
                    title        = t.name,
                    artist       = primaryArtist.name,
                    artistId     = primaryArtist.id,                // <— supply the missing field
                    albumArtUrl  = t.album.images.firstOrNull()?.url.orEmpty(),
                    previewUrl   = t.preview_url,
                    spotifyUrl   = t.external_urls["spotify"].orEmpty()
                )
            }
        } catch (e: HttpException) {
            if (e.code() == 401) {
                TokenManager(context).clearToken()
            }
            throw e
        }
    }

    suspend fun getRecentlyPlayed(): List<RecentlyPlayedSong> {
        val token = tokenProvider()
            ?: throw IllegalStateException("No access token available")

        val response = apiService.getRecentlyPlayed("Bearer $token")
        return response.items.map { played ->
            RecentlyPlayedSong(
                id         = played.track.id,
                title      = played.track.name,
                artists    = played.track.artists.joinToString(", ") { it.name },
                albumArtUrl= played.track.album.images.firstOrNull()?.url.orEmpty(),
                playedAt   = played.played_at
            )
        }
    }

    suspend fun getArtistInfo(artistId: String): ArtistResponse {
        // Note: SpotifyApiService.getArtist only wants the ID, not the token header
        return apiService.getArtist(artistId)
    }

    suspend fun getArtistTopTracks(artistId: String): List<Track> {
        val token = tokenProvider()
            ?: throw IllegalStateException("No access token available")

        // This endpoint DOES want your Bearer token:
        val response = apiService.getArtistTopTracks("Bearer $token", artistId)
        return response.items
    }
}
