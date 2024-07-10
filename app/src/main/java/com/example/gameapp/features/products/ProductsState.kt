package com.example.gameapp.features.products

import com.example.gameapp.core.AsyncResult
import com.example.gameapp.core.Uninitialized
import com.example.gameapp.data.model.ProductsResponse

data class ProductsState(
    val productsResponse: AsyncResult<ProductsResponse> = Uninitialized
)