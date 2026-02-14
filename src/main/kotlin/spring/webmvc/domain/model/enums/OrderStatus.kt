package spring.webmvc.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class OrderStatus(
    val description: String,
) {
    ORDER("주문"),
    CONFIRM("확정"),
    CANCEL("취소"),
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun of(name: String?): OrderStatus? {
            if (name.isNullOrBlank()) return null

            return entries.find { it.name == name }
        }
    }
}
