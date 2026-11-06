package com.kuit.week6.presentation.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuit.week6.domain.product.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(ProductsUiState())
    val state = _state.asStateFlow()

    init {
        load()
    }

    fun onIntent(intent: ProductsIntent) {
        when (intent) {
            ProductsIntent.LoadMore, ProductsIntent.Retry -> load()
        }
    }

    private fun load() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.hasNext) return

        val isFirstPage = current.items.isEmpty()
        _state.update {
            it.copy(isLoading = isFirstPage, isLoadingMore = !isFirstPage, error = null)
        }

        viewModelScope.launch {
            try {
                val result = getProducts(current.page)
                _state.update {
                    it.copy(
                        items = it.items + result.items,
                        page = it.page + 1,
                        total = result.total,
                        hasNext = result.hasNext,
                        isLoading = false,
                        isLoadingMore = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                val message = if (e.code() == 401) "로그인이 만료되었습니다. 다시 로그인하세요" else "서버 오류 (${e.code()})"
                showError(message)
            } catch (e: IOException) {
                showError("인터넷 연결을 확인하세요")
            }
        }
    }

    private fun showError(message: String) {
        _state.update { it.copy(isLoading = false, isLoadingMore = false, error = message) }
    }
}