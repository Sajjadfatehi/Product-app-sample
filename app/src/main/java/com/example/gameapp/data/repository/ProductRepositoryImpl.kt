package com.example.gameapp.data.repository

import com.example.gameapp.data.model.ProductsResponse
import com.example.gameapp.data.remotedatasource.ProductRemoteDataSource
import com.example.gameapp.data.utils.safeApiCall
import com.example.gameapp.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val dataSource: ProductRemoteDataSource) :
    ProductRepository {

    override suspend fun fetchProducts(): ProductsResponse {
        return dataSource.fetchProducts().safeApiCall()
    }
}