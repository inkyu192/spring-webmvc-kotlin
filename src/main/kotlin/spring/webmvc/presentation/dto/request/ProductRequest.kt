package spring.webmvc.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import spring.webmvc.application.dto.command.AccommodationPutCommand
import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.command.TransportPutCommand
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureProperty
import java.time.Instant

data class ProductPutRequest(
    val status: ProductStatus,
    val category: ProductCategory,
    val name: String,
    val description: String,

    @field:Min(100)
    val price: Long,

    @field:Max(9999)
    val quantity: Long,

    @field:JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
        property = "category",
    )
    @field:JsonSubTypes(
        JsonSubTypes.Type(name = "TRANSPORT", value = TransportPutRequest::class),
        JsonSubTypes.Type(name = "ACCOMMODATION", value = AccommodationPutRequest::class),
    )
    val property: ProductPropertyPutRequest,

    val exposureProperty: ProductExposureProperty,
) {
    fun toCommand(
        id: Long? = null,
    ): ProductPutCommand {
        val commandProperty = when (property) {
            is TransportPutRequest -> TransportPutCommand(
                departureLocation = property.departureLocation,
                arrivalLocation = property.arrivalLocation,
                departureTime = property.departureTime,
                arrivalTime = property.arrivalTime,
            )

            is AccommodationPutRequest -> AccommodationPutCommand(
                place = property.place,
                checkInTime = property.checkInTime,
                checkOutTime = property.checkOutTime,
            )
        }

        return ProductPutCommand(
            id = id,
            status = status,
            category = category,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            property = commandProperty,
            exposureProperty = exposureProperty,
        )
    }
}

sealed interface ProductPropertyPutRequest

data class TransportPutRequest(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductPropertyPutRequest

data class AccommodationPutRequest(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductPropertyPutRequest