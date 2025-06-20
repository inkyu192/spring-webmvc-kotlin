package spring.webmvc.domain.cache

import java.time.Duration


enum class CacheKey(
    private val key: String,
    val timeOut: Duration? = null,
) {
    PRODUCT(key = "product:%d", timeOut = Duration.ofHours(1)),
    PRODUCT_STOCK(key = "product:%d:stock"),
    CURATION(key = "curation", timeOut = Duration.ofHours(1)),
    CURATION_PRODUCT(key = "curation:%d:product", timeOut = Duration.ofHours(1)),
    REFRESH_TOKEN(key = "member:%s:token:refresh", timeOut = Duration.ofDays(7)),
    REQUEST_LOCK(key = "request-lock:%d:%s:%s", timeOut = Duration.ofSeconds(1));

    fun generate(vararg args: Any) = key.format(args = args)
}