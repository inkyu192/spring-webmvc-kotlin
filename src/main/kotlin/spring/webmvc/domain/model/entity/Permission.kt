package spring.webmvc.domain.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Permission protected constructor(
    val name: String,
) : BaseTime() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set
}