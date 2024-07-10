package com.example.gameapp.features.productdetail

import com.example.gameapp.core.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor() : BaseViewModel<ProductDetailState,ProductDetailAction,ProductDetailEffect>(
    ProductDetailState()
) {

    override fun onEachAction(action: ProductDetailAction) {
        TODO("Not yet implemented")
    }
}