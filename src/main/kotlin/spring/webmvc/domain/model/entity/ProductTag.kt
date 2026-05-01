package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class ProductTag protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    val tag: Tag,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(product: Product, tag: Tag) = ProductTag(product = product, tag = tag)
    }
}
