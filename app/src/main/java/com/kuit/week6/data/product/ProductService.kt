package com.kuit.week6.data.product

import retrofit2.http.GET
import retrofit2.http.Query

interface ProductService {

    @GET("auth/products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 20,
        @Query("skip") skip: Int,
    ): ProductsPage
}