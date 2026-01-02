package spring.webmvc.presentation.controller

import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.command.ProductDeleteCommand
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.presentation.dto.request.ProductPutRequest
import spring.webmvc.presentation.dto.response.ProductPageResponse
import spring.webmvc.presentation.dto.response.ProductResponse

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    fun findProducts(
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false) name: String?,
    ): ProductPageResponse {
        val page = productService.findProducts(
            cursorId = cursorId,
            size = size,
            name = name,
        )

        return ProductPageResponse.from(page)
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    fun findProduct(
        @PathVariable id: Long,
        @RequestParam category: Category,
    ): ProductResponse {
        val result = productService.findProduct(category = category, id = id)

        return ProductResponse.from(result)
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(
        @RequestBody @Validated request: ProductPutRequest,
    ): ProductResponse {
        val command = request.toCommand()
        val productResult = productService.createProduct(command)

        return ProductResponse.from(productResult)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody @Validated request: ProductPutRequest,
    ): ProductResponse {
        val command = request.toCommand(id = id)
        val productResult = productService.updateProduct(command)

        return ProductResponse.from(productResult)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteProduct(
        @PathVariable id: Long,
        @RequestParam category: Category,
    ) {
        val command = ProductDeleteCommand(id = id, category = category)
        productService.deleteProduct(command)
    }
}