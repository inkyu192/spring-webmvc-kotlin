package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.CurationAttributeConverter
import spring.webmvc.domain.converter.CurationExposureAttributeConverter
import spring.webmvc.domain.model.enums.CurationLayout
import spring.webmvc.domain.model.enums.CurationPlacement
import spring.webmvc.domain.model.enums.CurationType
import spring.webmvc.domain.model.vo.CurationAttribute
import spring.webmvc.domain.model.vo.CurationExposureAttribute

@Entity
class Curation protected constructor(
    val title: String,

    @field:Enumerated(EnumType.STRING)
    val placement: CurationPlacement,

    @field:Enumerated(EnumType.STRING)
    val type: CurationType,

    @field:Convert(converter = CurationAttributeConverter::class)
    val attribute: CurationAttribute,

    @field:Convert(converter = CurationExposureAttributeConverter::class)
    val exposureAttribute: CurationExposureAttribute,

    val isExposed: Boolean,

    val sortOrder: Long,
) : BaseCreator() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @OneToMany(mappedBy = "curation", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _curationProducts = mutableListOf<CurationProduct>()

    @get:Transient
    val curationProducts: List<CurationProduct>
        get() = _curationProducts.toList()

    companion object {
        fun create(
            title: String,
            placement: CurationPlacement,
            type: CurationType = CurationType.MANUAL,
            attribute: CurationAttribute = CurationAttribute(tagIds = emptyList()),
            exposureAttribute: CurationExposureAttribute = CurationExposureAttribute(layout = CurationLayout.CAROUSEL),
            isExposed: Boolean,
            sortOrder: Long,
        ) = Curation(
            title = title,
            placement = placement,
            type = type,
            attribute = attribute,
            exposureAttribute = exposureAttribute,
            isExposed = isExposed,
            sortOrder = sortOrder,
        )
    }

    fun addProduct(product: Product, sortOrder: Long) {
        val curationProduct = CurationProduct.create(
            curation = this,
            product = product,
            sortOrder = sortOrder,
        )

        _curationProducts.add(curationProduct)
    }
}
