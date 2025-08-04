package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class MenuPermission protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val menu: Menu,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val permission: Permission,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    companion object {
        fun create(menu: Menu, permission: Permission) = MenuPermission(menu = menu, permission = permission)
    }
}