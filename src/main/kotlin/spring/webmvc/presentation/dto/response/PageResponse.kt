package spring.webmvc.presentation.dto.response

import org.springframework.data.domain.Page
import spring.webmvc.infrastructure.persistence.dto.CursorPage

data class OffsetPageResponse(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
) {
    companion object {
        fun from(page: Page<*>): OffsetPageResponse {
            return OffsetPageResponse(
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious(),
            )
        }
    }
}

data class CursorPageResponse(
    val size: Int,
    val hasNext: Boolean?,
    val nextCursorId: Long?,
) {
    companion object {
        fun from(page: CursorPage<*>): CursorPageResponse {
            return CursorPageResponse(
                size = page.size,
                hasNext = page.hasNext,
                nextCursorId = page.nextCursorId,
            )
        }
    }
}