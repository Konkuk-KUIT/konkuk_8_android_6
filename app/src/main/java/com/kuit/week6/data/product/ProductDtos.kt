package com.kuit.week6.data.product

import com.kuit.week6.domain.product.Product
import kotlinx.serialization.Serializable

@Serializable
data class ProductsPage(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)

@Serializable
data class ProductDto(
    val id: Int,
    val title: String,
    val brand: String? = null,
    val price: Double,
    val thumbnail: String? = null,
)

fun ProductDto.toProduct(): Product = Product(
    id = id,
    title = title,
    brand = brand,
    price = price,
)