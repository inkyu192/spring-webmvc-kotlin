package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.Category
import java.time.Instant

@Entity
class Flight protected constructor(
    airline: String,
    flightNumber: String,
    departureAirport: String,
    arrivalAirport: String,
    departureTime: Instant,
    arrivalTime: Instant,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "product_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val product: Product,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var airline = airline
        protected set

    var flightNumber = flightNumber
        protected set

    var departureAirport = departureAirport
        protected set

    var arrivalAirport = arrivalAirport
        protected set

    var departureTime = departureTime
        protected set

    var arrivalTime = arrivalTime
        protected set

    companion object {
        fun create(
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            airline: String,
            flightNumber: String,
            departureAirport: String,
            arrivalAirport: String,
            departureTime: Instant,
            arrivalTime: Instant,
        ) = Flight(
            airline = airline,
            flightNumber = flightNumber,
            departureAirport = departureAirport,
            arrivalAirport = arrivalAirport,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
            product = Product.create(
                category = Category.FLIGHT,
                name = name,
                description = description,
                price = price,
                quantity = quantity
            ),
        )
    }

    fun update(
        name: String,
        description: String,
        price: Long,
        quantity: Long,
        airline: String,
        flightNumber: String,
        departureAirport: String,
        arrivalAirport: String,
        departureTime: Instant,
        arrivalTime: Instant,
    ) {
        this.product.update(name, description, price, quantity)
        this.airline = airline
        this.flightNumber = flightNumber
        this.departureAirport = departureAirport
        this.arrivalAirport = arrivalAirport
        this.departureTime = departureTime
        this.arrivalTime = arrivalTime
    }
}