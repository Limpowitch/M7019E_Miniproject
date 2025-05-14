package com.example.spotifysonglistapp.auth

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.spotifysonglistapp.util.SpotifySecrets
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data: Uri? = intent?.data
        if (data != null && data.toString().startsWith(SpotifySecrets.REDIRECT_URI)) {
            val code = data.getQueryParameter("code")

            if (code != null) {
                Log.d("AuthActivity", "Authorization code: $code")

                val tokenManager = TokenManager(this)
                val verifier = tokenManager.getCodeVerifier()

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val service = SpotifyAuthService.create()
                        val response = service.exchangeCodeForToken(
                            clientId = SpotifySecrets.CLIENT_ID,
                            code = code,
                            redirectUri = SpotifySecrets.REDIRECT_URI,
                            codeVerifier = verifier ?: ""
                        )

                        if (response.isSuccessful) {
                            val tokenResponse = response.body()
                            val token = tokenResponse?.access_token

                            if (token != null) {
                                Log.d("AuthActivity", "Access token received: $token")
                                tokenManager.saveToken(token)
                                tokenManager.clearCodeVerifier()

                                val resultIntent = Intent().apply {
                                    putExtra("access_token", token)
                                }
                                setResult(RESULT_OK, resultIntent)
                            } else {
                                Log.e("AuthActivity", "Token response body was null or malformed")
                                setResult(RESULT_CANCELED)
                            }
                        } else {
                            val error = response.errorBody()?.string()
                            Log.e("AuthActivity", "Token exchange failed: $error")
                            setResult(RESULT_CANCELED)
                        }
                    } catch (e: Exception) {
                        Log.e("AuthActivity", "Exception during token exchange", e)
                        setResult(RESULT_CANCELED)
                    }

                    finish()
                }
            } else {
                Log.e("AuthActivity", "No authorization code found")
                setResult(RESULT_CANCELED)
                finish()
            }
        } else {
            Log.e("AuthActivity", "Unexpected redirect URI: $data")
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
