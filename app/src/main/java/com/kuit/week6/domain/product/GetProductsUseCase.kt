package com.kuit.week6.domain.product

import javax.inject.Inject

class GetProductsUseCase @Inject constructor(private val repo: ProductRepository) {
    suspend operator fun invoke(page: Int): ProductPage = repo.getProducts(page)
}