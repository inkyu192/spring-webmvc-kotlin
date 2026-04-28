package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class Menu protected constructor(
    val translationCode: String,

    val path: String?,

    val sortOrder: Long?,

    @field:ManyToOne(fetch = FetchType.LAZY)
    @field:JoinColumn(name = "parent_id")
    val parent: Menu?,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(translationCode: String, path: String? = null, parent: Menu? = null, sortOrder: Long? = null) =
            Menu(translationCode = translationCode, path = path, sortOrder = sortOrder, parent = parent)
    }
}
