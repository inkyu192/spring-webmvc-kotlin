package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
class UserDevice protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
    val deviceId: String,
    val deviceName: String,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    var lastLoginAt: Instant = Instant.now()
        protected set

    companion object {
        const val MAX_DEVICES = 5

        fun create(
            user: User,
            deviceId: String,
            deviceName: String,
        ) = UserDevice(
            user = user,
            deviceId = deviceId,
            deviceName = deviceName,
        )
    }

    fun updateLastLoginAt() {
        this.lastLoginAt = Instant.now()
    }
}
