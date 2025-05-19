package com.example.spotifysonglistapp.network

import com.example.spotifysonglistapp.models.ArtistResponse
import com.example.spotifysonglistapp.models.ArtistTopTracksResponse
import com.example.spotifysonglistapp.models.RecentlyPlayedResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import com.example.spotifysonglistapp.models.TopTracksResponse
import com.example.spotifysonglistapp.models.RecentlyPlayedSong
import retrofit2.http.Path


interface SpotifyApiService {

    @GET("v1/me/top/tracks")
    suspend fun getTopTracks(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 20,
        @Query("time_range") timeRange: String = "short_term"
    ): TopTracksResponse

    @GET("v1/me/player/recently-played")
    suspend fun getRecentlyPlayed(
        @Header("Authorization") authHeader: String,
        @Query("limit") limit: Int = 50
    ): RecentlyPlayedResponse

    @GET("v1/artists/{id}")
    suspend fun getArtist(
        @Path("id") artistId: String
    ): ArtistResponse

    @GET("v1/artists/{id}/top-tracks")
    suspend fun getArtistTopTracks(
        @Path("id") artistId: String,
        @Query("market") market: String = "SE"
    ): ArtistTopTracksResponse

    companion object {
        fun create(tokenProvider: () -> String?): SpotifyApiService {
            val authInterceptor = Interceptor { chain ->
                val token = tokenProvider()
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.spotify.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(SpotifyApiService::class.java)
        }
    }
}
