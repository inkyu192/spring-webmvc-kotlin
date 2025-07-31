package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.CryptoAttributeConverter
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.LocalDate

@Entity
class Member protected constructor(
    @Embedded
    val email: Email,
    password: String,
    name: String,
    phone: Phone,
    birthDate: LocalDate,
) : BaseTime() {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    var password = password
        protected set

    @Convert(converter = CryptoAttributeConverter::class)
    var name = name
        protected set

    @Embedded
    var phone = phone
        protected set

    var birthDate = birthDate
        protected set

    @OneToMany(mappedBy = "member", cascade = [(CascadeType.ALL)])
    private val _memberRoles = mutableListOf<MemberRole>()

    @get:Transient
    val memberRoles: List<MemberRole>
        get() = _memberRoles.toList()

    @OneToMany(mappedBy = "member", cascade = [(CascadeType.ALL)])
    private val _memberPermissions = mutableListOf<MemberPermission>()

    @get:Transient
    val memberPermissions: List<MemberPermission>
        get() = _memberPermissions.toList()

    companion object {
        fun create(
            email: String,
            password: String,
            name: String,
            phone: String,
            birthDate: LocalDate,
        ) = Member(
            email = Email.create(email),
            password = password,
            name = name,
            phone = Phone.create(phone),
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
        phone?.let { this.phone = Phone.create(phone) }
        birthDate?.let { this.birthDate = birthDate }
    }
}
