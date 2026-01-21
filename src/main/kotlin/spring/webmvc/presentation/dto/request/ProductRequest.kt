package spring.webmvc.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import spring.webmvc.application.dto.command.AccommodationPutCommand
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.application.dto.command.ProductUpdateCommand
import spring.webmvc.application.dto.command.TransportPutCommand
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureAttribute
import java.time.Instant

data class ProductCreateRequest(
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
    val attribute: ProductAttributePutRequest,

    val exposureAttribute: ProductExposureAttribute,
) {
    fun toCommand(): ProductCreateCommand {
        val commandAttribute = when (attribute) {
            is TransportPutRequest -> TransportPutCommand(
                departureLocation = attribute.departureLocation,
                arrivalLocation = attribute.arrivalLocation,
                departureTime = attribute.departureTime,
                arrivalTime = attribute.arrivalTime,
            )

            is AccommodationPutRequest -> AccommodationPutCommand(
                place = attribute.place,
                checkInTime = attribute.checkInTime,
                checkOutTime = attribute.checkOutTime,
            )
        }

        return ProductCreateCommand(
            category = category,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            attribute = commandAttribute,
            exposureAttribute = exposureAttribute,
        )
    }
}

data class ProductUpdateRequest(
    val status: ProductStatus,
    val name: String,
    val description: String,

    @field:Min(100)
    val price: Long,

    @field:Max(9999)
    val quantity: Long,

    @field:JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
    @field:JsonSubTypes(
        JsonSubTypes.Type(value = TransportPutRequest::class),
        JsonSubTypes.Type(value = AccommodationPutRequest::class),
    )
    val attribute: ProductAttributePutRequest,

    val exposureAttribute: ProductExposureAttribute,
) {
    fun toCommand(id: Long): ProductUpdateCommand {
        val commandAttribute = when (attribute) {
            is TransportPutRequest -> TransportPutCommand(
                departureLocation = attribute.departureLocation,
                arrivalLocation = attribute.arrivalLocation,
                departureTime = attribute.departureTime,
                arrivalTime = attribute.arrivalTime,
            )

            is AccommodationPutRequest -> AccommodationPutCommand(
                place = attribute.place,
                checkInTime = attribute.checkInTime,
                checkOutTime = attribute.checkOutTime,
            )
        }

        return ProductUpdateCommand(
            id = id,
            status = status,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            attribute = commandAttribute,
            exposureAttribute = exposureAttribute,
        )
    }
}

sealed interface ProductAttributePutRequest

data class TransportPutRequest(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributePutRequest

data class AccommodationPutRequest(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributePutRequest
