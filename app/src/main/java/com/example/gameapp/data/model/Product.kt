package com.example.gameapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val available: Boolean,
    val color: String,
    val colorCode: String,
    val description: String,
    val id: Int,
    val imageURL: String,
    val longDescription: String,
    val name: String,
    val price: Price,
    val rating: Double,
    val releaseDate: Int,
    val type: String
)