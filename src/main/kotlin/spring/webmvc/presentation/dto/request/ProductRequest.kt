package spring.webmvc.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import spring.webmvc.application.dto.command.*
import spring.webmvc.domain.model.enums.Category
import spring.webmvc.domain.model.enums.ProductStatus
import java.time.Instant

data class ProductCreateRequest(
    val category: Category,
    val name: String,
    val description: String,
    @field:Min(100)
    val price: Long,
    @field:Max(9999)
    val quantity: Long,
    val attribute: ProductAttributeCreateRequest,
) {
    fun toCommand(): ProductCreateCommand {
        val commandAttribute = when (attribute) {
            is TransportCreateRequest -> TransportCreateCommand(
                departureLocation = attribute.departureLocation,
                arrivalLocation = attribute.arrivalLocation,
                departureTime = attribute.departureTime,
                arrivalTime = attribute.arrivalTime,
            )

            is AccommodationCreateRequest -> AccommodationCreateCommand(
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
        )
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    visible = false
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TransportCreateRequest::class, name = "TRANSPORT"),
    JsonSubTypes.Type(value = AccommodationCreateRequest::class, name = "ACCOMMODATION"),
)
sealed interface ProductAttributeCreateRequest

data class TransportCreateRequest(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributeCreateRequest

data class AccommodationCreateRequest(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributeCreateRequest

data class ProductUpdateRequest(
    val status: ProductStatus,
    val name: String,
    val description: String,
    @field:Min(100)
    val price: Long,
    @field:Max(9999)
    val quantity: Long,
    val attribute: ProductAttributeUpdateRequest,
) {
    fun toCommand(id: Long): ProductUpdateCommand {
        val commandAttribute = when (attribute) {
            is TransportUpdateRequest -> TransportUpdateCommand(
                departureLocation = attribute.departureLocation,
                arrivalLocation = attribute.arrivalLocation,
                departureTime = attribute.departureTime,
                arrivalTime = attribute.arrivalTime,
            )

            is AccommodationUpdateRequest -> AccommodationUpdateCommand(
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
        )
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    visible = false
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TransportUpdateRequest::class, name = "TRANSPORT"),
    JsonSubTypes.Type(value = AccommodationUpdateRequest::class, name = "ACCOMMODATION"),
)
sealed interface ProductAttributeUpdateRequest

data class TransportUpdateRequest(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributeUpdateRequest

data class AccommodationUpdateRequest(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributeUpdateRequest