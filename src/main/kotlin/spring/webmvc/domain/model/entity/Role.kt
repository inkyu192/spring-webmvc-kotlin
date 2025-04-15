package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class Role protected constructor(
    name: String,
) : BaseTime() {
    @Id
    @GeneratedValue
    @Column(name = "role_id")
    var id: Long? = null
        protected set

    var name: String = name
        protected set

    @OneToMany(mappedBy = "role", cascade = [(CascadeType.ALL)])
    private val _rolePermissions: MutableList<RolePermission> = mutableListOf()

    @get:Transient
    val rolePermissions: List<RolePermission>
        get() = _rolePermissions.toList()

    companion object {
        fun create(name: String) = Role(name)
    }

    fun addPermission(permission: Permission) {
        _rolePermissions.add(RolePermission.create(role = this, permission = permission))
    }
}