package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.vo.Email
import java.time.Instant

@Entity
@Table(name = "user_credential")
class UserCredential protected constructor(
    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Embedded
    val email: Email,

    password: String,
) {
    @Id
    @Column(name = "user_id")
    var id: Long? = null
        protected set

    var password: String = password
        protected set

    var verifiedAt: Instant? = null
        protected set

    companion object {
        fun create(
            user: User,
            email: Email,
            password: String,
        ) = UserCredential(
            user = user,
            email = email,
            password = password,
        )
    }

    fun verify() {
        this.verifiedAt = Instant.now()
    }

    fun updatePassword(newPassword: String) {
        this.password = newPassword
    }

    fun isVerified() = verifiedAt != null
}