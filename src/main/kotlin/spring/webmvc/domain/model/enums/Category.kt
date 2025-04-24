package spring.webmvc.domain.model.enums

enum class Category(
    val description: String
) {
    ROLE_BOOK("책"),
    ROLE_TICKET("표"),
    FLIGHT("항공권"),
    ACCOMMODATION("숙박"),
    TICKET("티켓");
}