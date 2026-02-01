package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.ProductExposureAttributeConverter
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureAttribute
import spring.webmvc.infrastructure.exception.InvalidEntityStatusException

@Entity
@Table(name = "product")
class Product(
    @Enumerated(EnumType.STRING)
    val category: ProductCategory,

    @Enumerated(EnumType.STRING)
    var status: ProductStatus,

    var name: String,
    var description: String,
    var price: Long,
    var quantity: Long,

    @Convert(converter = ProductExposureAttributeConverter::class)
    var exposureAttribute: ProductExposureAttribute,
) : BaseCreator() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
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
                fromStatus = this.status.description,
                toStatus = status.description,
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
