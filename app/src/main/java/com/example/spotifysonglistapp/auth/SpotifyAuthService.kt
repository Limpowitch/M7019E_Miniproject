package com.example.spotifysonglistapp.auth

import retrofit2.http.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface SpotifyAuthService {
    @FormUrlEncoded
    @POST("api/token")
    suspend fun exchangeCodeForToken(
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code_verifier") codeVerifier: String
    ): Response<TokenResponse>

    companion object {
        fun create(): SpotifyAuthService {
            return Retrofit.Builder()
                .baseUrl("https://accounts.spotify.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(SpotifyAuthService::class.java)
        }
    }
}
