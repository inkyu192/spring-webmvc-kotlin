package spring.webmvc.domain.model.vo

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import spring.webmvc.domain.converter.CryptoAttributeConverter

@Embeddable
class Phone protected constructor(
    @Convert(converter = CryptoAttributeConverter::class)
    @Column(name = "phone")
    val value: String,
) {
    companion object {
        private val PHONE_PATTERN = Regex("^01[016789]-\\d{3,4}-\\d{4}$")

        fun create(value: String): Phone {
            require(PHONE_PATTERN.matches(value)) {
                "Invalid phone number: $value"
            }
            return Phone(value)
        }
    }
}