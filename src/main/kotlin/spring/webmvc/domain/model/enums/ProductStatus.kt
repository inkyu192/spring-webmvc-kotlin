package spring.webmvc.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class ProductStatus(
    val description: String,
) {
    PENDING("대기"),
    SELLING("판매중"),
    DISCONTINUED("중지"),
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun of(name: String?): ProductStatus? {
            if (name.isNullOrBlank()) return null

            return entries.find { it.name == name }
        }
    }
}
