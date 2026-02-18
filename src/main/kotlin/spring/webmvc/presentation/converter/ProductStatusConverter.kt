package spring.webmvc.presentation.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.enums.ProductStatus

@Component
class ProductStatusConverter : Converter<String, ProductStatus> {
    override fun convert(source: String) = ProductStatus.of(source)
}
