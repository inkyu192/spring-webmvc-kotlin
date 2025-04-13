package spring.webmvc.domain.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import spring.webmvc.infrastructure.util.crypto.CryptoUtil

@Component
@Converter
class CryptoAttributeConverter(
    @Qualifier("hexAESCryptoUtil")
    private val cryptoUtil: CryptoUtil,
) : AttributeConverter<String?, String?> {
    override fun convertToDatabaseColumn(attribute: String?) = attribute?.let { cryptoUtil.encrypt(it) }
    override fun convertToEntityAttribute(dbData: String?) = dbData?.let { cryptoUtil.decrypt(it) }
}
