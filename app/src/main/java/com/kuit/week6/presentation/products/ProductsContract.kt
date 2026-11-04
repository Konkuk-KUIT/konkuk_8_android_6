package com.kuit.week6.presentation.products

import com.kuit.week6.domain.product.Product

data class ProductsUiState(
    val items: List<Product> = emptyList(),
    val page: Int = 0,
    val total: Int = 0,
    val hasNext: Boolean = true,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
)

sealed interface ProductsIntent {
    data object LoadMore : ProductsIntent
    data object Retry : ProductsIntent
}