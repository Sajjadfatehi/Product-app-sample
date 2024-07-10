package com.example.gameapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Header(
    val headerDescription: String,
    val headerTitle: String
)