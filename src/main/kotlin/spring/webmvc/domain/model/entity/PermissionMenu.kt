package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class PermissionMenu protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val permission: Permission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val menu: Menu,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    companion object {
        fun create(menu: Menu, permission: Permission) = PermissionMenu(menu = menu, permission = permission)
    }
}