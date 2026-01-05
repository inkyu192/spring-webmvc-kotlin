package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.CurationCategory

@Entity
class Curation protected constructor(
    title: String,
    category: CurationCategory,
    isExposed: Boolean,
    sortOrder: Long,
) : BaseCreator() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var title = title
        protected set

    @Enumerated(EnumType.STRING)
    var category = category
        protected set

    var isExposed = isExposed
        protected set

    var sortOrder = sortOrder
        protected set

    @OneToMany(mappedBy = "curation", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _curationProducts = mutableListOf<CurationProduct>()

    @get:Transient
    val curationProducts: List<CurationProduct>
        get() = _curationProducts.toList()

    companion object {
        fun create(title: String, category: CurationCategory, isExposed: Boolean, sortOrder: Long) =
            Curation(title = title, category = category, isExposed = isExposed, sortOrder = sortOrder)
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