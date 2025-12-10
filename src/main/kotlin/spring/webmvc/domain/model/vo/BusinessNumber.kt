package spring.webmvc.domain.model.vo

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import spring.webmvc.domain.converter.CryptoAttributeConverter

@Embeddable
class BusinessNumber protected constructor(
    @Convert(converter = CryptoAttributeConverter::class)
    @Column(name = "business_number")
    val value: String,
) {
    companion object {
        private val BUSINESS_NUMBER_PATTERN = Regex("^\\d{3}-\\d{2}-\\d{5}$")

        fun create(value: String): BusinessNumber {
            require(BUSINESS_NUMBER_PATTERN.matches(value)) {
                "Invalid business number format: $value (expected: xxx-xx-xxxxx)"
            }
            return BusinessNumber(value)
        }
    }
}