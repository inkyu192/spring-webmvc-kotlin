package spring.webmvc.domain.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.crypto.CryptoService

@Component
@Converter
class CryptoAttributeConverter(
    private val aesHexCryptoService: CryptoService,
) : AttributeConverter<String?, String?> {
    override fun convertToDatabaseColumn(attribute: String?) = attribute?.let { aesHexCryptoService.encrypt(it) }
    override fun convertToEntityAttribute(dbData: String?) = dbData?.let { aesHexCryptoService.decrypt(it) }
}
