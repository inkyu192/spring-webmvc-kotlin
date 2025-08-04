package spring.webmvc.presentation.dto.request

import spring.webmvc.application.dto.command.TicketUpdateCommand
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TicketUpdateRequest(
    category: Category,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    val place: String,
    val performanceTime: Instant,
    val duration: String,
    val ageLimit: String,
) : ProductUpdateRequest(
    category = category,
    name = name,
    description = description,
    price = price,
    quantity = quantity,
) {
    override fun toCommand() =
        TicketUpdateCommand(
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