package com.kuit.week6.presentation.products

import com.kuit.week6.domain.product.Product

data class ProductsUiState(
    val items: List<Product> = emptyList(),
    val page: Int = 0,
    val hasNext: Boolean = true,
    val isLoading: Boolean = false,      // 첫 페이지 로딩
    val isLoadingMore: Boolean = false,  // 다음 페이지 로딩
    val error: String? = null,
)

sealed interface ProductsIntent {
    data object LoadMore : ProductsIntent
    data object Retry : ProductsIntent
}
