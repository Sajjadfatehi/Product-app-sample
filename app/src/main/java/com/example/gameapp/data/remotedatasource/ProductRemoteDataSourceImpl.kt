package com.example.gameapp.data.remotedatasource

import com.example.gameapp.data.apiservice.ProductService
import com.example.gameapp.data.model.ProductsResponse
import retrofit2.Response
import javax.inject.Inject

class ProductRemoteDataSourceImpl @Inject constructor(private val productService: ProductService) : ProductRemoteDataSource {

    override suspend fun fetchProducts(): Response<ProductsResponse> {
        return productService.getProducts()
    }
}