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

    val sortOrder: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(curation: Curation, product: Product, sortOrder: Long) =
            CurationProduct(curation = curation, product = product, sortOrder = sortOrder)
    }
}