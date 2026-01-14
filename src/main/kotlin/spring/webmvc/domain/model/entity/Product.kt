package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.ProductExposurePropertyConverter
import spring.webmvc.domain.model.enums.ProductCategory
import spring.webmvc.domain.model.enums.ProductStatus
import spring.webmvc.domain.model.vo.ProductExposureProperty
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

    @Convert(converter = ProductExposurePropertyConverter::class)
    var exposureProperty: ProductExposureProperty,
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
            status: ProductStatus = ProductStatus.PENDING,
            exposureProperty: ProductExposureProperty,
        ) = Product(
            category = category,
            status = status,
            name = name,
            description = description,
            price = price,
            quantity = quantity,
            exposureProperty = exposureProperty,
        )
    }

    fun replace(
        status: ProductStatus,
        name: String,
        description: String,
        price: Long,
        quantity: Long,
        exposureProperty: ProductExposureProperty,
    ) {
        if (status != ProductStatus.PENDING) {
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
        this.exposureProperty = exposureProperty
    }

    fun removeQuantity(quantity: Long) {
        this.quantity -= quantity
    }

    fun addQuantity(quantity: Long) {
        this.quantity += quantity
    }
}