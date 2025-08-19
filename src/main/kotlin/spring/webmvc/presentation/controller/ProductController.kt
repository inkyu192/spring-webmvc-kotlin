package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.presentation.dto.request.ProductCreateRequest
import spring.webmvc.presentation.dto.request.ProductUpdateRequest
import spring.webmvc.presentation.dto.response.*

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findProducts(
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) name: String?,
    ) = ProductPageResponse(
        page = productService.findProducts(
            cursorId = cursorId,
            size = size,
            name = name,
        )
    )

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findProduct(
        @PathVariable id: Long,
        @RequestParam category: Category,
    ) = toProductResponse(productResult = productService.findProduct(category = category, id = id))

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@RequestBody @Validated productCreateRequest: ProductCreateRequest): ProductResponse {
        val command = productCreateRequest.toCommand()
        val productResult = productService.createProduct(command)
        return toProductResponse(productResult)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody @Validated productUpdateRequest: ProductUpdateRequest
    ): ProductResponse {
        val command = productUpdateRequest.toCommand()
        val productResult = productService.updateProduct(id = id, productUpdateCommand = command)
        return toProductResponse(productResult)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(@PathVariable id: Long, @RequestParam category: Category) {
        productService.deleteProduct(category = category, id = id)
    }

    private fun toProductResponse(productResult: ProductResult): ProductResponse {
        return when (productResult.category) {
            Category.TICKET -> TicketResponse(productResult as TicketResult)
            Category.FLIGHT -> FlightResponse(productResult as FlightResult)
            Category.ACCOMMODATION -> AccommodationResponse(productResult as AccommodationResult)
        }
    }
}