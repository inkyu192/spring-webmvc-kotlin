package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class RolePermission protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    val permission: Permission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    val role: Role,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    companion object {
        fun create(role: Role, permission: Permission) = RolePermission(role = role, permission = permission)
    }
}