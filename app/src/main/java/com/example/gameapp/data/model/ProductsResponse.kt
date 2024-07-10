package com.example.gameapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductsResponse(
    val filters: List<String>,
    val header: Header,
    val products: List<Product>
)