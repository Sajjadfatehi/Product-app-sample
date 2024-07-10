package com.example.gameapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Price(
    val currency: String,
    val value: Double
)