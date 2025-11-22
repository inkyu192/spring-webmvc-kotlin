package spring.webmvc.domain.model.enums

enum class MemberType(
    val description: String
) {
    OPERATOR("운영자"),
    PARTNER("판매자"),
    CUSTOMER("구매자");
}
