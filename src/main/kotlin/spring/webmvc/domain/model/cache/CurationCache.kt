package spring.webmvc.domain.model.cache

data class CurationCache(
    val id: Long,
    val title: String,
) {
    companion object {
        fun create(id: Long, title: String) =
            CurationCache(
                id = id,
                title = title,
            )
    }
}