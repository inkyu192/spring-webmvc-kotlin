package spring.webmvc.presentation.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.dto.result.AccommodationResult
import spring.webmvc.application.dto.result.FlightResult
import spring.webmvc.application.dto.result.TicketResult
import spring.webmvc.application.service.ProductService
import spring.webmvc.domain.model.enums.Category
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
    ): ProductResponse {
        val productResult = productService.findProduct(id, category)

        return when (productResult.category) {
            Category.TICKET -> TicketResponse(productResult as TicketResult)
            Category.FLIGHT -> FlightResponse(productResult as FlightResult)
            Category.ACCOMMODATION -> AccommodationResponse(productResult as AccommodationResult)
        }
    }
}