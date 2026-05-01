package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class Tag protected constructor(
    val name: String,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(name: String) = Tag(name)
    }
}
