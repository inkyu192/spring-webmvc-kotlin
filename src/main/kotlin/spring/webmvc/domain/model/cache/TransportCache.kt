package spring.webmvc.domain.model.cache

import java.time.Instant

data class TransportCache(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
    val quantity: Long,
    val createdAt: Instant,
    val transportId: Long,
    val departureLocation: String,
    val arrivalLocation: String,
    val departureTime: Instant,
    val arrivalTime: Instant,
) {
    companion object {
        fun create(
            id: Long,
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            createdAt: Instant,
            transportId: Long,
            departureLocation: String,
            arrivalLocation: String,
            departureTime: Instant,
            arrivalTime: Instant,
        ) = TransportCache(
            id = id,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            createdAt = createdAt,
            transportId = transportId,
            departureLocation = departureLocation,
            arrivalLocation = arrivalLocation,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
        )
    }
}
