package spring.webmvc.infrastructure.persistence.dto

data class CursorPage<T>(
    val content: List<T>,
    val size: Int,
    val hasNext: Boolean,
    val nextCursorId: Long?,
) {
    companion object {
        fun <T> create(
            content: List<T>,
            size: Int,
            getCursorId: (T) -> Long?,
        ): CursorPage<T> {
            val actualContent = if (content.size > size) {
                content.dropLast(1)
            } else {
                content
            }

            val cursorId = if (content.size > size) {
                getCursorId(content.last())
            } else {
                null
            }

            return CursorPage(
                content = actualContent,
                size = size,
                hasNext = content.size > size,
                nextCursorId = cursorId
            )
        }
    }

    fun <U> map(transform: (T) -> U) = CursorPage(
        content = content.map(transform),
        size = size,
        hasNext = hasNext,
        nextCursorId = nextCursorId,
    )
}