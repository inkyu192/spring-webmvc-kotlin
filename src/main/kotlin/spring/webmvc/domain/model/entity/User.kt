package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.CryptoAttributeConverter
import spring.webmvc.domain.model.enums.UserStatus
import spring.webmvc.domain.model.enums.UserType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant
import java.time.LocalDate

@Entity
class User protected constructor(
    @Embedded
    val email: Email,
    password: String,
    name: String,
    phone: Phone,
    birthDate: LocalDate,
    type: UserType,
    status: UserStatus,
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
    var status: UserStatus = status
        protected set

    var deletedAt: Instant? = null
        protected set

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _userRoles = mutableListOf<UserRole>()

    @get:Transient
    val userRoles: List<UserRole>
        get() = _userRoles.toList()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _userPermissions = mutableListOf<UserPermission>()

    @get:Transient
    val userPermissions: List<UserPermission>
        get() = _userPermissions.toList()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _userCompanies = mutableListOf<UserCompany>()

    @get:Transient
    val userCompanies: List<UserCompany>
        get() = _userCompanies.toList()

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    private val _deliveryAddresses = mutableListOf<DeliveryAddress>()

    @get:Transient
    val deliveryAddresses: List<DeliveryAddress>
        get() = _deliveryAddresses.toList()

    companion object {
        fun create(
            email: Email,
            password: String,
            name: String,
            phone: Phone,
            birthDate: LocalDate,
            type: UserType,
        ) = User(
            email = email,
            password = password,
            name = name,
            phone = phone,
            birthDate = birthDate,
            type = type,
            status = UserStatus.PENDING,
        )
    }

    fun addRole(role: Role) {
        _userRoles.add(UserRole.create(user = this, role = role))
    }

    fun addPermission(permission: Permission) {
        _userPermissions.add(UserPermission.create(user = this, permission = permission))
    }

    fun update(name: String?, phone: Phone?, birthDate: LocalDate?) {
        name?.let { this.name = name }
        phone?.let { this.phone = phone }
        birthDate?.let { this.birthDate = birthDate }
    }

    fun updateStatus(status: UserStatus) {
        this.status = status
        if (status == UserStatus.WITHDRAWN) {
            this.deletedAt = Instant.now()
        }
    }

    fun updatePassword(newPassword: String) {
        this.password = newPassword
    }

    fun isNotActive() = this.status != UserStatus.ACTIVE
}
