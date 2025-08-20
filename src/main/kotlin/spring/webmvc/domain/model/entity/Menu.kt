package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class Menu protected constructor(
    name: String,
    path: String?,
    parent: Menu?,
) : BaseTime() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var name = name
        protected set

    var path = path
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    var parent = parent
        protected set

    @OneToMany(mappedBy = "parent")
    private val _children = mutableListOf<Menu>()

    @OneToMany(mappedBy = "menu", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _permissionMenus = mutableListOf<PermissionMenu>()

    @get:Transient
    val children: List<Menu>
        get() = _children.toList()

    @get:Transient
    val permissionMenus: List<PermissionMenu>
        get() = _permissionMenus.toList()

    companion object {
        fun create(name: String, path: String? = null, parent: Menu? = null) =
            Menu(name = name, path = path, parent = parent)
    }

    fun addPermission(permission: Permission) {
        _permissionMenus.add(PermissionMenu.create(menu = this, permission = permission))
    }
}