package spring.webmvc.domain.model.enums

enum class UserType(
    val description: String,
) {
    OPERATOR("운영자"),
    PARTNER("판매자"),
    CUSTOMER("구매자"),
    ;
}
