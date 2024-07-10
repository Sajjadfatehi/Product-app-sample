package com.example.gameapp.features.productdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

@Composable
fun ProductDetailScreen(navController: NavController) {
    val viewModel = hiltViewModel<ProductDetailViewModel>()

    ProductDetailScreen(viewModel)
}

@Composable
internal fun ProductDetailScreen(viewModel: ProductDetailViewModel) {
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    ProductDetailScreen(viewState) {

    }
}

@Composable
internal fun ProductDetailScreen(viewState: ProductDetailState, onAction: (ProductDetailAction) -> Unit) {

}
