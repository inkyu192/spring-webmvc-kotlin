package spring.webmvc.presentation.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.command.AccommodationCreateCommand
import spring.webmvc.application.dto.command.AccommodationUpdateCommand
import spring.webmvc.application.dto.command.FlightCreateCommand
import spring.webmvc.application.dto.command.FlightUpdateCommand
import spring.webmvc.application.dto.command.TicketCreateCommand
import spring.webmvc.application.dto.command.TicketUpdateCommand
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.ProductResult
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.presentation.dto.request.AccommodationCreateRequest
import spring.webmvc.presentation.dto.request.AccommodationUpdateRequest
import spring.webmvc.presentation.dto.request.FlightCreateRequest
import spring.webmvc.presentation.dto.request.FlightUpdateRequest
import spring.webmvc.presentation.dto.request.TicketCreateRequest
import spring.webmvc.presentation.dto.request.ProductCreateRequest
import spring.webmvc.presentation.dto.request.ProductUpdateRequest
import spring.webmvc.presentation.dto.request.TicketUpdateRequest
import spring.webmvc.presentation.dto.response.AccommodationResponse
import spring.webmvc.presentation.dto.response.FlightResponse
import spring.webmvc.presentation.dto.response.ProductResponse
import spring.webmvc.presentation.dto.response.TicketResponse

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
) {
    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findProducts(
        @PageableDefault pageable: Pageable,
        @RequestParam(required = false) name: String?,
    ) = productService.findProducts(pageable = pageable, name = name).map { ProductResponse(productResult = it) }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_READER')")
    fun findProduct(
        @PathVariable id: Long,
        @RequestParam category: Category,
    ) = toProductResponse(productResult = productService.findProduct(id, category))

    @PostMapping
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@RequestBody @Validated productCreateRequest: ProductCreateRequest): ProductResponse {
        val command = when (productCreateRequest.category) {
            Category.TICKET -> TicketCreateCommand(productCreateRequest as TicketCreateRequest)
            Category.FLIGHT -> FlightCreateCommand(productCreateRequest as FlightCreateRequest)
            Category.ACCOMMODATION -> AccommodationCreateCommand(productCreateRequest as AccommodationCreateRequest)
        }

        val productResult = productService.createProduct(command)

        return toProductResponse(productResult)
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PRODUCT_WRITER')")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody @Validated productUpdateRequest: ProductUpdateRequest
    ): ProductResponse {
        val command = when (productUpdateRequest.category) {
            Category.TICKET -> TicketUpdateCommand(productUpdateRequest as TicketUpdateRequest)
            Category.FLIGHT -> FlightUpdateCommand(productUpdateRequest as FlightUpdateRequest)
            Category.ACCOMMODATION -> AccommodationUpdateCommand(productUpdateRequest as AccommodationUpdateRequest)
        }

        val productResult = productService.updateProduct(productId = id, productUpdateCommand = command)

        return toProductResponse(productResult)
    }

    private fun toProductResponse(productResult: ProductResult): ProductResponse {
        return when (productResult.category) {
            Category.TICKET -> TicketResponse(productResult as TicketResult)
            Category.FLIGHT -> FlightResponse(productResult as FlightResult)
            Category.ACCOMMODATION -> AccommodationResponse(productResult as AccommodationResult)
        }
    }
}