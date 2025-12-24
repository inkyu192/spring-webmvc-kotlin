package spring.webmvc.application.dto.result

import spring.webmvc.domain.model.cache.TransportCache
import spring.webmvc.domain.model.entity.Transport
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

class TransportResult : ProductResult {
    val transportId: Long
    val departureLocation: String
    val arrivalLocation: String
    val departureTime: Instant
    val arrivalTime: Instant

    constructor(transport: Transport) : super(product = transport.product) {
        this.transportId = checkNotNull(transport.id)
        this.departureLocation = transport.departureLocation
        this.arrivalLocation = transport.arrivalLocation
        this.departureTime = transport.departureTime
        this.arrivalTime = transport.arrivalTime
    }

    constructor(transportCache: TransportCache) : super(
        id = transportCache.id,
        category = Category.TRANSPORT,
        name = transportCache.name,
        description = transportCache.description,
        price = transportCache.price,
        quantity = transportCache.quantity,
        createdAt = transportCache.createdAt
    ) {
        this.transportId = transportCache.transportId
        this.departureLocation = transportCache.departureLocation
        this.arrivalLocation = transportCache.arrivalLocation
        this.departureTime = transportCache.departureTime
        this.arrivalTime = transportCache.arrivalTime
    }
}
