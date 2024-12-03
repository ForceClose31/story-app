package com.example.storyapp.utils

import retrofit2.Response
import java.io.IOException

object ErrorParser {
    fun <T> parseError(response: Response<T>): String {
        return try {
            response.errorBody()?.string() ?: "Unknown error"
        } catch (e: IOException) {
            "Error parsing response"
        }
    }
}
