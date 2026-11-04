package com.kuit.week6.presentation.products

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kuit.week6.domain.product.Product
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    state: ProductsUiState,
    onIntent: (ProductsIntent) -> Unit,
    onBack: () -> Unit,
) {
    val listState = rememberLazyListState()
    val itemCount = state.items.size

    val reachedEnd by remember(itemCount) {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            itemCount > 0 && lastVisible >= itemCount - 3
        }
    }

    LaunchedEffect(reachedEnd, itemCount) {
        if (reachedEnd) onIntent(ProductsIntent.LoadMore)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("상품") },
                navigationIcon = { TextButton(onClick = onBack) { Text("←") } },
                actions = {
                    if (state.total > 0) {
                        Text("$itemCount / ${state.total}", modifier = Modifier.padding(end = 16.dp))
                    }
                },
            )
        }
    ) { padding ->
        when {
            state.isLoading && state.items.isEmpty() -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            state.error != null && state.items.isEmpty() -> Box(
                Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ErrorWithRetry(state.error) { onIntent(ProductsIntent.Retry) }
            }

            else -> LazyColumn(
                state = listState,
                contentPadding = padding,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.items, key = { it.id }) { product ->
                    ProductRow(product)
                    HorizontalDivider()
                }

                when {
                    state.isLoadingMore -> item(key = "loading") {
                        Box(
                            Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    }

                    state.error != null -> item(key = "error") {
                        ErrorWithRetry(state.error, Modifier.fillMaxWidth().padding(16.dp)) {
                            onIntent(ProductsIntent.Retry)
                        }
                    }

                    !state.hasNext -> item(key = "end") {
                        Box(
                            Modifier.fillMaxWidth().padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("${itemCount}개 모두 불러왔어요", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductRow(product: Product) {
    val price = "$" + String.format(Locale.US, "%.2f", product.price)

    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            "#${product.id}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Column {
            Text(
                product.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                listOfNotNull(product.brand, price).joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun ErrorWithRetry(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(message, color = MaterialTheme.colorScheme.error)
        OutlinedButton(onClick = onRetry) { Text("다시 시도") }
    }
}