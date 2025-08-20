package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class Curation protected constructor(
    title: String,
    isExposed: Boolean,
    sortOrder: Long,
) : BaseCreator() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var title = title
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
        fun create(title: String, isExposed: Boolean, sortOrder: Long) =
            Curation(title = title, isExposed = isExposed, sortOrder = sortOrder)
    }

    fun addProduct(curationProduct: CurationProduct) {
        _curationProducts.add(curationProduct)
    }
}