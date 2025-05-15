package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class Menu protected constructor(
    name: String,
    path: String?
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
    var parent: Menu? = null
        protected set

    @OneToMany(mappedBy = "parent")
    private val _children = mutableListOf<Menu>()

    @OneToMany(mappedBy = "menu", cascade = [(CascadeType.ALL)])
    private val _menuPermissions = mutableListOf<MenuPermission>()

    @get:Transient
    val children: List<Menu>
        get() = _children.toList()

    @get:Transient
    val menuPermissions: List<MenuPermission>
        get() = _menuPermissions.toList()

    companion object {
        fun create(name: String, path: String? = null) = Menu(name = name, path = path)
    }

    fun updateParent(parent: Menu) {
        this.parent = parent
        parent._children.add(this)
    }

    fun addPermission(permission: Permission) {
        _menuPermissions.add(MenuPermission.create(menu = this, permission = permission))
    }
}