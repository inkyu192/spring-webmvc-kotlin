package spring.webmvc.domain.model.enums

enum class Category(
    val description: String,
) {
    FLIGHT("항공권"),
    ACCOMMODATION("숙박"),
    TICKET("티켓"),
    ;
}