package spring.webmvc.application.dto.command

import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TicketCreateCommand(
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    val place: String,
    val performanceTime: Instant,
    val duration: String,
    val ageLimit: String,
) : ProductCreateCommand(
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity,
)