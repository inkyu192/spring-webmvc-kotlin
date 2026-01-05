package spring.webmvc.domain.model.enums

enum class ProductStatus(
    val description: String,
) {
    PENDING("대기"),
    SELLING("판매중"),
    DISCONTINUED("중지"),
    ;
}
