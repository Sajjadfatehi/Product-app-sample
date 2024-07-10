package com.example.gameapp.features.products

import com.example.gameapp.core.BaseViewModel
import com.example.gameapp.domain.usecase.FetchProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val fetchProductsUseCase: FetchProductsUseCase
) :
    BaseViewModel<ProductsState, ProductsAction, ProductsEffect>(ProductsState()) {
    //TODO: header in the fucking house

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        suspend {
            fetchProductsUseCase()
        }.execute {
            copy(productsResponse = it)
        }
    }

    override fun onEachAction(action: ProductsAction) {

    }
}