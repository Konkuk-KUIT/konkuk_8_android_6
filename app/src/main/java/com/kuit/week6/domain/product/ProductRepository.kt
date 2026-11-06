package com.kuit.week6.domain.product

interface ProductRepository {
    suspend fun getProducts(page: Int): ProductPage
}