package spring.webmvc.presentation.dto.request

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
) : ProductUpdateRequest(category, name, description, price, quantity)