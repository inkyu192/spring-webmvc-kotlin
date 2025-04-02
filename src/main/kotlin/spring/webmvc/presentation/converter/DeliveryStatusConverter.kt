package spring.webmvc.presentation.converter

import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.enums.DeliveryStatus

@Component
class DeliveryStatusConverter : Converter<String, DeliveryStatus> {
    override fun convert(source: String) = DeliveryStatus.of(source)
}