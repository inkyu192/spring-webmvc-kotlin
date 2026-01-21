package spring.webmvc.presentation.dto.response

import org.springframework.data.domain.Page
import spring.webmvc.infrastructure.persistence.dto.CursorPage

data class OffsetPageResponse<T>(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
    val content: List<T>,
) {
    companion object {
        fun <U, T> of(
            page: Page<U>,
            transform: (U) -> T,
        ) = OffsetPageResponse(
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext(),
            hasPrevious = page.hasPrevious(),
            content = page.content.map(transform),
        )
    }
}

data class CursorPageResponse<T>(
    val size: Long,
    val hasNext: Boolean,
    val nextCursorId: Long?,
    val content: List<T>,
) {
    companion object {
        fun <U, T> of(
            page: CursorPage<U>,
            transform: (U) -> T,
        ) = CursorPageResponse(
            content = page.content.map(transform),
            size = page.size,
            hasNext = page.hasNext,
            nextCursorId = page.nextCursorId,
        )
    }
}