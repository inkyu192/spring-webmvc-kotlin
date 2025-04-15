package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class RolePermission protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val permission: Permission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val role: Role,
) {
    @Id
    @GeneratedValue
    @Column(name = "role_permission_id")
    var id: Long? = null
        protected set

    companion object {
        fun create(role: Role, permission: Permission) = RolePermission(role = role, permission = permission)
    }
}