package spring.webmvc.presentation.controller.partner

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.query.ProductOffsetPageQuery
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.presentation.dto.request.ProductCreateRequest
import spring.webmvc.presentation.dto.request.ProductUpdateRequest
import spring.webmvc.presentation.dto.response.OffsetPageResponse
import spring.webmvc.presentation.dto.response.ProductDetailResponse
import spring.webmvc.presentation.dto.response.ProductSummaryResponse

@RestController("partnerProductController")
@RequestMapping("/partner/products")
class ProductController(
    private val productService: ProductService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    fun findProducts(
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) status: ProductStatus?,
    ): OffsetPageResponse<ProductSummaryResponse> {
        val query = ProductOffsetPageQuery(
            pageable = pageable,
            name = name,
            status = status,
        )

        val page = productService.findProductsWithOffsetPage(query = query)

        return OffsetPageResponse.of(page) { ProductSummaryResponse.of(result = it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    fun findProduct(
        @PathVariable id: Long,
    ): ProductDetailResponse {
        val result = productService.findProduct(id)

        return ProductDetailResponse.of(result)
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(
        @RequestBody @Validated request: ProductCreateRequest,
    ): ProductDetailResponse {
        val command = request.toCommand()
        val productResult = productService.createProduct(command)

        return ProductDetailResponse.of(productResult)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody @Validated request: ProductUpdateRequest,
    ): ProductDetailResponse {
        val command = request.toCommand(id)
        val productResult = productService.updateProduct(command)

        return ProductDetailResponse.of(productResult)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(
        @PathVariable id: Long,
    ) {
        productService.deleteProduct(id)
    }
}
