package spring.webmvc.presentation.dto.request

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
    val checkOutTime: Instant
) : ProductUpdateRequest(category, name, description, price, quantity)