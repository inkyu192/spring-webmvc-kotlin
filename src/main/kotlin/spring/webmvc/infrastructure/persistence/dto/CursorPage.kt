package spring.webmvc.infrastructure.persistence.dto

data class CursorPage<T>(
    val content: List<T>,
    val size: Int,
    val hasNext: Boolean,
    val nextCursorId: Long?,
) {
    constructor(
        content: List<T>,
        size: Int,
        getCursorId: (T) -> Long?,
    ) : this(
        content = if (content.size > size) content.dropLast(1) else content,
        size = size,
        hasNext = content.size > size,
        nextCursorId = if (content.size > size) content.last().let(getCursorId) else null
    )

    fun <U> map(transform: (T) -> U) =
        CursorPage(
            content = content.map(transform),
            size = size,
            hasNext = hasNext,
            nextCursorId = nextCursorId
        )
}