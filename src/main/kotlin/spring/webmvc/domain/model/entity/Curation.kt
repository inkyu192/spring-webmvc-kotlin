package spring.webmvc.domain.model.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Transient

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

    @OneToMany(mappedBy = "curation", cascade = [(CascadeType.ALL)])
    private val _curationProducts = mutableListOf<CurationProduct>()

    @get:Transient
    val curationProducts: List<CurationProduct>
        get() = _curationProducts.toList()

    companion object {
        fun create(title: String, isExposed: Boolean, sortOrder: Long) =
            Curation(title = title, isExposed = isExposed, sortOrder = sortOrder)
    }

    fun addProduct(product: Product, sortOrder: Long) {
        _curationProducts.add(CurationProduct.create(curation = this, product = product, sortOrder = sortOrder))
    }
}