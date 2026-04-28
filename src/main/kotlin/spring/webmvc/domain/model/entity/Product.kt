package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.ProductExposureAttributeConverter
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureAttribute
import spring.webmvc.infrastructure.exception.InvalidEntityStatusException

@Entity
class Product protected constructor(
    @field:Enumerated(EnumType.STRING)
    val category: ProductCategory,

    status: ProductStatus,
    name: String,
    description: String,
    price: Long,
    quantity: Long,
    exposureAttribute: ProductExposureAttribute,
) : BaseCreator() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    var status = status
        protected set

    var name = name
        protected set

    var description = description
        protected set

    var price = price
        protected set

    var quantity = quantity
        protected set

    @Convert(converter = ProductExposureAttributeConverter::class)
    var exposureAttribute = exposureAttribute
        protected set

    companion object {
        fun create(
            category: ProductCategory,
            name: String,
            description: String,
            price: Long,
            quantity: Long,
            exposureAttribute: ProductExposureAttribute,
        ) = Product(
            category = category,
            status = ProductStatus.PENDING,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            exposureAttribute = exposureAttribute,
        )
    }

    fun update(
        status: ProductStatus,
        name: String,
        description: String,
        price: Long,
        quantity: Long,
        exposureAttribute: ProductExposureAttribute,
    ) {
        if (this.status != ProductStatus.PENDING) {
            throw InvalidEntityStatusException(
                kClass = Product::class,
                id = checkNotNull(id),
                fromStatus = this.status.name,
                toStatus = status.name,
            )
        }

        this.status = status
        this.name = name
        this.description = description
        this.price = price
        this.quantity = quantity
        this.exposureAttribute = exposureAttribute
    }

    fun removeQuantity(quantity: Long) {
        this.quantity -= quantity
    }

    fun addQuantity(quantity: Long) {
        this.quantity += quantity
    }
}
