package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.DeviceType
import java.time.Instant

@Entity
class UserDevice protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
    val deviceId: String,
    val deviceName: String,
    @Enumerated(EnumType.STRING)
    val deviceType: DeviceType,
    token: String,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var lastLoginAt: Instant = Instant.now()
        protected set

    var token: String = token
        protected set

    companion object {
        const val MAX_DEVICES = 5

        fun create(
            user: User,
            deviceId: String,
            deviceName: String,
            deviceType: DeviceType,
            token: String,
        ) = UserDevice(
            user = user,
            deviceId = deviceId,
            deviceName = deviceName,
            deviceType = deviceType,
            token = token,
        )
    }

    fun updateLastLoginAt() {
        this.lastLoginAt = Instant.now()
    }

    fun updateToken(token: String) {
        this.token = token
    }
}
