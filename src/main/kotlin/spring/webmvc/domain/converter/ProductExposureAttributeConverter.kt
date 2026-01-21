package spring.webmvc.domain.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.vo.ProductExposureAttribute

@Component
@Converter
class ProductExposureAttributeConverter(
    private val objectMapper: ObjectMapper,
) : AttributeConverter<ProductExposureAttribute, String?> {
    override fun convertToDatabaseColumn(attribute: ProductExposureAttribute): String? =
        objectMapper.writeValueAsString(attribute)

    override fun convertToEntityAttribute(dbData: String?) =
        dbData?.let { objectMapper.readValue<ProductExposureAttribute>(it) }
}
