package com.kuit.week6.data.product

import com.kuit.week6.domain.product.ProductPage
import com.kuit.week6.domain.product.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val service: ProductService,
) : ProductRepository {

    override suspend fun getProducts(page: Int): ProductPage {
        val response = service.getProducts(limit = PAGE_SIZE, skip = page * PAGE_SIZE)
        return ProductPage(
            items = response.products.map { it.toProduct() },
            hasNext = response.skip + response.products.size < response.total,
            total = response.total,
        )
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}