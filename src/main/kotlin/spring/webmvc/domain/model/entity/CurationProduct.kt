package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class CurationProduct protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curation_id")
    val curation: Curation,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var sortOrder: Long? = null
        protected set

    companion object {
        fun create(curation: Curation, product: Product) =
            CurationProduct(curation = curation, product = product)
    }
}