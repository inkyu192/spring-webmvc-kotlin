package spring.webmvc.presentation.dto.request

import spring.webmvc.application.dto.command.AccommodationUpdateCommand
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class AccommodationUpdateRequest(
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductUpdateRequest(
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity,
) {
    override fun toCommand() =
        AccommodationUpdateCommand(
            category = category,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            place = place,
            checkInTime = checkInTime,
            checkOutTime = checkOutTime,
        )
}