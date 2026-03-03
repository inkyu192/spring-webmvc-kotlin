package spring.webmvc.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class OrderStatus {
    ORDER,
    CONFIRM,
    CANCEL,
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
