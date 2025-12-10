package spring.webmvc.domain.model.vo

import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embeddable
import spring.webmvc.domain.converter.CryptoAttributeConverter

@Embeddable
class Email protected constructor(
    @Convert(converter = CryptoAttributeConverter::class)
    @Column(name = "email")
    val value: String,
) {
    companion object {
        private val EMAIL_PATTERN = Regex("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")

        fun create(value: String): Email {
            require(EMAIL_PATTERN.matches(value)) {
                "Invalid email address: $value"
            }
            return Email(value)
        }
    }
}