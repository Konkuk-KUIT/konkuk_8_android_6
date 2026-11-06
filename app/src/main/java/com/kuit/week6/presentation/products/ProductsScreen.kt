package com.kuit.week6.presentation.products

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kuit.week6.domain.product.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    state: ProductsUiState,
    onIntent: (ProductsIntent) -> Unit,
) {
    val listState = rememberLazyListState()

    // 마지막으로 보이는 항목이 끝에서 3개 안에 들어오면 다음 페이지를 요청한다
    val reachedEnd by remember(state.items.size) {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            state.items.isNotEmpty() && lastVisible >= state.items.size - 3
        }
    }
    LaunchedEffect(reachedEnd, state.items.size) {
        if (reachedEnd) onIntent(ProductsIntent.LoadMore)
    }

    Scaffold(topBar = { TopAppBar(title = { Text("상품 목록") }) }) { padding ->
        if (state.isLoading) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(state = listState, modifier = Modifier.padding(padding).fillMaxSize()) {
                items(state.items, key = { it.id }) { product -> ProductRow(product) }

                if (state.isLoadingMore) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                state.error?.let { msg ->
                    item {
                        Column(
                            Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(msg)
                            Button(onClick = { onIntent(ProductsIntent.Retry) }) { Text("재시도") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductRow(product: Product) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(product.title)
        product.brand?.let { Text(it) }
        Text("\$${product.price}")
    }
}
