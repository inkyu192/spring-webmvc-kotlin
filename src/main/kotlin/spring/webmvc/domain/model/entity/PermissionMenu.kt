package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class PermissionMenu protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    val permission: Permission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    val menu: Menu,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(menu: Menu, permission: Permission) = PermissionMenu(menu = menu, permission = permission)
    }
}