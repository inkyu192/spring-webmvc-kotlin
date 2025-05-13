package spring.webmvc.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import spring.webmvc.domain.model.enums.Category

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "category",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TicketUpdateRequest::class, name = "TICKET"),
    JsonSubTypes.Type(value = FlightUpdateRequest::class, name = "FLIGHT"),
    JsonSubTypes.Type(value = AccommodationUpdateRequest::class, name = "ACCOMMODATION"),
)
open class ProductUpdateRequest(
    val category: Category,
    val name: String,
    val description: String,
    @field:Min(100)
    val price: Int,
    @field:Max(9999)
    val quantity: Int,
)