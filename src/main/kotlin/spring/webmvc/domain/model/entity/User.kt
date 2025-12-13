package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.converter.CryptoAttributeConverter
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.vo.Phone
import java.time.LocalDate

@Entity
@Table(name = "users")
class User protected constructor(
    name: String,
    phone: Phone,
    gender: Gender,
    birthday: LocalDate,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    @Convert(converter = CryptoAttributeConverter::class)
    var name = name
        protected set

    @Embedded
    var phone = phone
        protected set

    @Enumerated(EnumType.STRING)
    var gender = gender
        protected set

    var birthday = birthday
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
            name: String,
            phone: Phone,
            gender: Gender,
            birthday: LocalDate,
        ) = User(
            name = name,
            phone = phone,
            gender = gender,
            birthday = birthday,
        )
    }

    fun addRole(role: Role) {
        _userRoles.add(UserRole.create(user = this, role = role))
    }

    fun addPermission(permission: Permission) {
        _userPermissions.add(UserPermission.create(user = this, permission = permission))
    }

    fun update(name: String?, phone: Phone?, birthday: LocalDate?, gender: Gender?) {
        name?.let { this.name = it }
        phone?.let { this.phone = it }
        birthday?.let { this.birthday = it }
        gender?.let { this.gender = it }
    }
}
