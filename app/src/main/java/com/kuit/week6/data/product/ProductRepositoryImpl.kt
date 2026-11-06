package com.kuit.week6.data.product

import com.kuit.week6.domain.product.ProductPage
import com.kuit.week6.domain.product.ProductRepository
import javax.inject.Inject

private const val PAGE_SIZE = 20

class ProductRepositoryImpl @Inject constructor(
    private val service: ProductService,
) : ProductRepository {

    override suspend fun getProducts(page: Int): ProductPage {
        val skip = page * PAGE_SIZE
        val res = service.getProducts(limit = PAGE_SIZE, skip = skip)
        // 서버가 skip/limit 을 무시하면 중복·누락이 생기므로 여기서 멈춘다
        check(res.skip == skip) { "서버가 skip 을 무시했습니다 (요청 $skip, 응답 ${res.skip})" }
        check(res.limit == PAGE_SIZE) { "서버가 limit 을 바꿨습니다 (요청 $PAGE_SIZE, 응답 ${res.limit})" }
        return ProductPage(
            items = res.products.map { it.toProduct() },
            // 빈 페이지가 와도 total 이 크면 무한 로딩되지 않도록 isNotEmpty 를 함께 본다
            hasNext = res.products.isNotEmpty() && res.skip + res.products.size < res.total,
        )
    }
}
