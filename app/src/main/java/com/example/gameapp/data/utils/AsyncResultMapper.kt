package com.example.gameapp.data.utils

import com.example.gameapp.core.AsyncResult
import com.example.gameapp.core.Fail
import com.example.gameapp.core.Success
import retrofit2.Response

fun <T> Response<T>.toAsyncResult(): AsyncResult<T> {
    return try {
        if (this.isSuccessful) {
            val body = this.body()
            if (body != null) {
                Success(body)
            } else {
                Fail(Exception("Response body is null"))
            }
        } else {
            Fail(Exception("Response error: ${this.code()} ${this.message()}"))
        }
    } catch (e: Exception) {
        Fail(e)
    }
}


fun <T> Response<T>.safeApiCall(): T {
    if (isSuccessful) {
        return body() ?: throw Exception("Response body is null")
    } else {
        throw Exception("Error response: ${message()}")
    }
}