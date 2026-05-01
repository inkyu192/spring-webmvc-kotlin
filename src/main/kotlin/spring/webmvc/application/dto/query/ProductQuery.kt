package spring.webmvc.application.dto.query

import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.enums.ProductStatus

data class ProductOffsetPageQuery(
    val pageable: Pageable,
    val name: String?,
    val status: ProductStatus?,
    val tagIds: List<Long> = emptyList(),
)

data class ProductCursorPageQuery(
    val cursorId: Long?,
    val name: String?,
    val status: ProductStatus?,
    val tagIds: List<Long> = emptyList(),
)
