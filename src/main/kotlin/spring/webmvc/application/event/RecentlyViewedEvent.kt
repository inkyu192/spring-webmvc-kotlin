package spring.webmvc.application.event

data class RecentlyViewedEvent(
    val userId: Long,
    val productId: Long,
)
