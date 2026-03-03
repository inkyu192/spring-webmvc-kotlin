package spring.webmvc.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator

enum class ProductStatus {
    PENDING,
    SELLING,
    DISCONTINUED,
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
