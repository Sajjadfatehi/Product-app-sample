package com.example.gameapp.domain.usecase

import com.example.gameapp.data.model.ProductsResponse
import com.example.gameapp.domain.repository.ProductRepository
import javax.inject.Inject

class FetchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): ProductsResponse {
        return productRepository.fetchProducts()
    }
}