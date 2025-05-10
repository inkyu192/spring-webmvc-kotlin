package spring.webmvc.presentation.dto.request

import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class AccommodationCreateRequest(
    category: Category,
    name: String,
    description: String,
    price: Int,
    quantity: Int,
    val place: String,
    val checkInTime: Instant,
    val checkOutTime: Instant
) : ProductCreateRequest(category, name, description, price, quantity)