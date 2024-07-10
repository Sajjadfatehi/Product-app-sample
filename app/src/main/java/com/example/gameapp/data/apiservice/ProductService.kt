package com.example.gameapp.data.apiservice

import com.example.gameapp.data.model.ProductsResponse
import retrofit2.Response
import retrofit2.http.GET

interface ProductService {

    @GET("products-test.json")
    suspend fun getProducts():Response<ProductsResponse>
}