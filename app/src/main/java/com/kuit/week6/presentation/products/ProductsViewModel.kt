package com.kuit.week6.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuit.week6.domain.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repo: ProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsUiState())
    val state = _state.asStateFlow()

    init { loadMore() }

    fun onIntent(intent: ProductsIntent) {
        when (intent) {
            ProductsIntent.LoadMore, ProductsIntent.Retry -> loadMore()
        }
    }

    private fun loadMore() {
        val current = _state.value
        // 가드: 이미 로딩 중이거나 마지막 페이지면 아무것도 하지 않는다
        if (current.isLoading || current.isLoadingMore || !current.hasNext) return

        val page = current.page
        // 요청 전에 로딩 상태를 먼저 켜서, 같은 페이지를 두 번 요청하지 않게 한다
        _state.update { it.copy(isLoading = page == 0, isLoadingMore = page > 0, error = null) }

        viewModelScope.launch {
            try {
                val res = repo.getProducts(page)
                _state.update {
                    it.copy(
                        items = it.items + res.items,
                        page = page + 1,
                        hasNext = res.hasNext,
                        isLoading = false,
                        isLoadingMore = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, isLoadingMore = false, error = "상품을 불러오지 못했습니다") }
            }
        }
    }
}
