package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class AccommodationCreateCommand(
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant,
) : ProductCreateCommand(
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity
)