package spring.webmvc.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import spring.webmvc.application.dto.command.ProductCreateCommand
import spring.webmvc.domain.model.enums.Category

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    property = "category",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = TransportCreateRequest::class, name = "TRANSPORT"),
    JsonSubTypes.Type(value = AccommodationCreateRequest::class, name = "ACCOMMODATION"),
)
abstract class ProductCreateRequest(
    val category: Category,
    val name: String,
    val description: String,
    @field:Min(100)
    val price: Long,
    @field:Max(9999)
    val quantity: Long,
) {
    abstract fun toCommand(): ProductCreateCommand
}