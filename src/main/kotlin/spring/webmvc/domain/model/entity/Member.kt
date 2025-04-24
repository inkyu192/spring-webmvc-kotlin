package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.CryptoAttributeConverter
import java.time.LocalDate

@Entity
class Member protected constructor(
    @Convert(converter = CryptoAttributeConverter::class)
    val account: String,
    password: String,
    name: String,
    phone: String,
    birthDate: LocalDate,
) : BaseTime() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var password: String = password
        protected set

    @Convert(converter = CryptoAttributeConverter::class)
    var name: String = name
        protected set

    @Convert(converter = CryptoAttributeConverter::class)
    var phone: String = phone
        protected set

    var birthDate: LocalDate = birthDate
        protected set

    @OneToMany(mappedBy = "member", cascade = [(CascadeType.ALL)])
    private val _memberRoles: MutableList<MemberRole> = mutableListOf()

    @get:Transient
    val memberRoles: List<MemberRole>
        get() = _memberRoles.toList()

    @OneToMany(mappedBy = "member", cascade = [(CascadeType.ALL)])
    private val _memberPermissions: MutableList<MemberPermission> = mutableListOf()

    @get:Transient
    val memberPermissions: List<MemberPermission>
        get() = _memberPermissions.toList()

    companion object {
        fun create(
            account: String,
            password: String,
            name: String,
            phone: String,
            birthDate: LocalDate,
        ) = Member(
            account = account,
            password = password,
            name = name,
            phone = phone,
            birthDate = birthDate,
        )
    }

    fun addRole(role: Role) {
        _memberRoles.add(MemberRole.create(member = this, role = role))
    }

    fun addPermission(permission: Permission) {
        _memberPermissions.add(MemberPermission.create(member = this, permission = permission))
    }

    fun update(password: String?, name: String?, phone: String?, birthDate: LocalDate?) {
        password?.let { this.password = password }
        name?.let { this.name = name }
        phone?.let { this.phone = phone }
        birthDate?.let { this.birthDate = birthDate }
    }
}
