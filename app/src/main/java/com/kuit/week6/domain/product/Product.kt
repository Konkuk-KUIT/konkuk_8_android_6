package com.kuit.week6.domain.product

data class Product(
    val id: Int,
    val title: String,
    val brand: String?,
    val price: Double,
    val thumbnailUrl: String,
)

data class ProductPage(
    val items: List<Product>,
    val hasNext: Boolean,
)