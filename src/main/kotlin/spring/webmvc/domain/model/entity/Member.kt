package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.CryptoAttributeConverter
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.enums.MemberType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant
import java.time.LocalDate

@Entity
class Member protected constructor(
    @Embedded
    val email: Email,
    password: String,
    name: String,
    phone: Phone,
    birthDate: LocalDate,
    type: MemberType,
    status: MemberStatus,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Enumerated(EnumType.STRING)
    var type = type
        protected set

    @Enumerated(EnumType.STRING)
    var status: MemberStatus = status
        protected set

    var deletedAt: Instant? = null
        protected set

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _memberRoles = mutableListOf<MemberRole>()

    @get:Transient
    val memberRoles: List<MemberRole>
        get() = _memberRoles.toList()

    @OneToMany(mappedBy = "member", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _memberPermissions = mutableListOf<MemberPermission>()

    @get:Transient
    val memberPermissions: List<MemberPermission>
        get() = _memberPermissions.toList()

    companion object {
        fun create(
            email: Email,
            password: String,
            name: String,
            phone: Phone,
            birthDate: LocalDate,
            type: MemberType,
        ) = Member(
            email = email,
            password = password,
            name = name,
            phone = phone,
            birthDate = birthDate,
            type = type,
            status = MemberStatus.PENDING,
        )
    }

    fun addRole(role: Role) {
        _memberRoles.add(MemberRole.create(member = this, role = role))
    }

    fun addPermission(permission: Permission) {
        _memberPermissions.add(MemberPermission.create(member = this, permission = permission))
    }

    fun update(name: String?, phone: Phone?, birthDate: LocalDate?) {
        name?.let { this.name = name }
        phone?.let { this.phone = phone }
        birthDate?.let { this.birthDate = birthDate }
    }

    fun updateStatus(status: MemberStatus) {
        this.status = status
        if (status == MemberStatus.WITHDRAWN) {
            this.deletedAt = Instant.now()
        }
    }

    fun updatePassword(newPassword: String) {
        this.password = newPassword
    }

    fun isNotActive() = this.status != MemberStatus.ACTIVE
}
