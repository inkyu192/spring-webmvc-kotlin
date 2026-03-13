package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class Menu protected constructor(
    translationCode: String,
    path: String?,
    sortOrder: Long?,
    parent: Menu?,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var translationCode = translationCode
        protected set

    var path = path
        protected set

    var sortOrder = sortOrder
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent = parent
        protected set

    companion object {
        fun create(translationCode: String, path: String? = null, parent: Menu? = null, sortOrder: Long? = null) =
            Menu(translationCode = translationCode, path = path, sortOrder = sortOrder, parent = parent)
    }
}
