package com.example.gameapp.data.remotedatasource

import com.example.gameapp.data.model.ProductsResponse
import retrofit2.Response

interface ProductRemoteDataSource {

    suspend fun fetchProducts() : Response<ProductsResponse>
}