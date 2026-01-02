package spring.webmvc.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import spring.webmvc.application.dto.command.AccommodationPutCommand
import spring.webmvc.application.dto.command.ProductPutCommand
import spring.webmvc.application.dto.command.TransportPutCommand
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

data class ProductPutRequest(
    val category: Category,
    val name: String,
    val description: String,
    @field:Min(100)
    val price: Long,
    @field:Max(9999)
    val quantity: Long,
    val detail: ProductDetailPutRequest,
) {
    fun toCommand(id: Long? = null): ProductPutCommand {
        val commandDetail = when (detail) {
            is TransportPutRequest -> TransportPutCommand(
                departureLocation = detail.departureLocation,
                arrivalLocation = detail.arrivalLocation,
                departureTime = detail.departureTime,
                arrivalTime = detail.arrivalTime,
            )

            is AccommodationPutRequest -> AccommodationPutCommand(
                place = detail.place,
                checkInTime = detail.checkInTime,
                checkOutTime = detail.checkOutTime,
            )
        }

        return ProductPutCommand(
            id = id,
            category = category,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            detail = commandDetail,
        )
    }
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "type",
    visible = false
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TransportPutRequest::class, name = "TRANSPORT"),
    JsonSubTypes.Type(value = AccommodationPutRequest::class, name = "ACCOMMODATION"),
)
sealed interface ProductDetailPutRequest

data class TransportPutRequest(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductDetailPutRequest

data class AccommodationPutRequest(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductDetailPutRequest