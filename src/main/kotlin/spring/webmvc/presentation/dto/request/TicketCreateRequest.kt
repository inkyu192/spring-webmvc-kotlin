package spring.webmvc.presentation.dto.request

import spring.webmvc.application.dto.command.TicketCreateCommand
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TicketCreateRequest(
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    val place: String,
    val performanceTime: Instant,
    val duration: String,
    val ageLimit: String,
) : ProductCreateRequest(
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity,
) {
    override fun toCommand() =
        TicketCreateCommand(
            category = category,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            place = place,
            performanceTime = performanceTime,
            duration = duration,
            ageLimit = ageLimit,
        )
}