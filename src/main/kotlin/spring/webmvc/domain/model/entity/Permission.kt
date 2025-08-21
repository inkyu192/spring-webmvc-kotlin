package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class Permission protected constructor(
    val name: String,
) : BaseTime() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    @OneToMany(mappedBy = "permission", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _permissionMenus = mutableListOf<PermissionMenu>()

    @get:Transient
    val permissionMenu: List<PermissionMenu>
        get() = _permissionMenus.toList()

    companion object {
        fun create(name: String) = Permission(name)
    }

    fun addMenu(menu: Menu) {
        _permissionMenus.add(PermissionMenu.create(permission = this, menu = menu))
        menu.addPermission(this)
    }
}