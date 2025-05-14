package com.example.spotifysonglistapp.auth

data class TokenResponse(
    val access_token: String,
    val token_type: String,
    val scope: String,
    val expires_in: Int,
    val refresh_token: String?
)
