package spring.webmvc.presentation.dto.request

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import spring.webmvc.application.dto.command.*
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
    fun toCommand() = ProductCreateCommand(
        category = category,
        name = name,
        description = description,
        price = price,
        quantity = quantity,
        attribute = attribute.toCommand(),
        exposureAttribute = exposureAttribute,
    )
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
    fun toCommand(id: Long) = ProductUpdateCommand(
        id = id,
        status = status,
        name = name,
        description = description,
        price = price,
        quantity = quantity,
        attribute = attribute.toCommand(),
        exposureAttribute = exposureAttribute,
    )
}

sealed interface ProductAttributePutRequest {
    fun toCommand(): ProductAttributePutCommand
}

data class TransportPutRequest(
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) : ProductAttributePutRequest {
    override fun toCommand() = TransportPutCommand(
        departureLocation = departureLocation,
        arrivalLocation = arrivalLocation,
        departureTime = departureTime,
        arrivalTime = arrivalTime,
    )
}

data class AccommodationPutRequest(
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductAttributePutRequest {
    override fun toCommand() = AccommodationPutCommand(
        place = place,
        checkInTime = checkInTime,
        checkOutTime = checkOutTime,
    )
}
