package spring.webmvc.presentation.controller.customer

import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.presentation.dto.response.CursorPageResponse
import spring.webmvc.presentation.dto.response.ProductDetailResponse
import spring.webmvc.presentation.dto.response.ProductSummaryResponse

@RestController("customerProductController")
@RequestMapping("/customer/products")
class ProductController(
    private val productService: ProductService,
) {
    @GetMapping
    fun findProducts(
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) name: String?,
    ): CursorPageResponse<ProductSummaryResponse> {
        val query = ProductCursorPageQuery(
            cursorId = cursorId,
            name = name,
            status = ProductStatus.SELLING,
        )

        val page = productService.findProductsWithCursorPage(query = query)

        return CursorPageResponse.from(page) { ProductSummaryResponse.from(result = it) }
    }

    @GetMapping("/{id}")
    fun findProduct(
        @PathVariable id: Long,
    ): ProductDetailResponse {
        val result = productService.findProductCached(id)

        productService.incrementProductViewCount(id)

        return ProductDetailResponse.from(result)
    }
}
