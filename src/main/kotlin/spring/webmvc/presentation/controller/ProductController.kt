package spring.webmvc.presentation.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import spring.webmvc.application.service.ProductService

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
    ) = productService.findProducts(pageable = pageable, name = name)
}