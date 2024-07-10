package com.example.gameapp.domain.repository

import com.example.gameapp.core.AsyncResult
import com.example.gameapp.data.model.ProductsResponse
import retrofit2.Response

interface ProductRepository {
    suspend fun fetchProducts(): ProductsResponse
}