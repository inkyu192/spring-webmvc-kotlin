package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class UserPermission protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    val permission: Permission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(user: User, permission: Permission) = UserPermission(user = user, permission = permission)
    }
}
