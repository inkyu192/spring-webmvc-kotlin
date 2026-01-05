package spring.webmvc.presentation.controller.customer

import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.query.ProductCursorPageQuery
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.presentation.dto.response.ProductCursorPageResponse
import spring.webmvc.presentation.dto.response.ProductDetailResponse

@RestController
@RequestMapping("/customer/products")
class CustomerProductController(
    private val productService: ProductService,
) {
    @GetMapping
    fun findProducts(
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) name: String?,
    ): ProductCursorPageResponse {
        val query = ProductCursorPageQuery(
            cursorId = cursorId,
            name = name,
            status = ProductStatus.SELLING,
        )

        val page = productService.findProductsWithCursorPage(query = query)

        return ProductCursorPageResponse.from(page)
    }

    @GetMapping("/{id}")
    fun findProduct(
        @PathVariable id: Long,
    ): ProductDetailResponse {
        val result = productService.findProduct(id)

        return ProductDetailResponse.from(result)
    }
}
