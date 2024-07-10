package com.example.gameapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gameapp.features.productdetail.ProductDetailScreen
import com.example.gameapp.features.products.ProductScreen

@Composable
fun MainNavGraph() {
    //TODO: animation
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ProductsDestination) {
        composable<ProductsDestination> {
            ProductScreen(navController = navController)
        }
        composable<ProductDetailDestination> {
            ProductDetailScreen(navController = navController)
        }
    }
}