package spring.webmvc.presentation.controller.customer

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import spring.webmvc.application.service.ProductService
import spring.webmvc.application.service.WishlistService
import spring.webmvc.infrastructure.security.SecurityContextUtil
import spring.webmvc.presentation.dto.request.WishlistCreateRequest
import spring.webmvc.presentation.dto.response.CursorPageResponse
import spring.webmvc.presentation.dto.response.ProductSummaryResponse
import spring.webmvc.presentation.dto.response.WishlistResponse

@RestController("customerMyController")
@RequestMapping("/customer/my")
class MyController(
    private val productService: ProductService,
    private val wishlistService: WishlistService,
) {
    @GetMapping("/recently-viewed")
    fun findRecentlyViewedProducts(
        @RequestParam(required = false) cursorId: Long?,
    ): CursorPageResponse<ProductSummaryResponse> {
        val userId = SecurityContextUtil.getUserId()
        val page = productService.findRecentlyViewedProducts(userId, cursorId)
        return CursorPageResponse.of(page) { ProductSummaryResponse.of(it) }
    }

    @GetMapping("/wishlists")
    fun findWishlists(
        @RequestParam(required = false) cursorId: Long?,
    ): CursorPageResponse<WishlistResponse> {
        val userId = SecurityContextUtil.getUserId()
        val page = wishlistService.findWishlists(userId, cursorId)
        return CursorPageResponse.of(page) { WishlistResponse.of(it) }
    }

    @PostMapping("/wishlists")
    @ResponseStatus(HttpStatus.CREATED)
    fun addWishlist(@RequestBody request: WishlistCreateRequest) {
        val userId = SecurityContextUtil.getUserId()
        wishlistService.addWishlist(userId, request.productId)
    }

    @DeleteMapping("/wishlists/{wishlistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun removeWishlist(@PathVariable wishlistId: Long) {
        val userId = SecurityContextUtil.getUserId()
        wishlistService.removeWishlist(userId, wishlistId)
    }
}
