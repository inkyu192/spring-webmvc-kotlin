package spring.webmvc.domain.model.entity

import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class CurationProduct protected constructor(
    sortOrder: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curation_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val curation: Curation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val product: Product,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var sortOrder = sortOrder
        protected set

    companion object {
        fun create(curation: Curation, product: Product, sortOrder: Long) =
            CurationProduct(sortOrder = sortOrder, curation = curation, product = product)
    }
}