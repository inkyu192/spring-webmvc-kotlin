package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.OauthProvider

@Entity
class UserOAuth protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Enumerated(EnumType.STRING)
    val oauthProvider: OauthProvider,

    val oauthUserId: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(
            user: User,
            oauthProvider: OauthProvider,
            oauthUserId: String,
        ) = UserOAuth(
            user = user,
            oauthProvider = oauthProvider,
            oauthUserId = oauthUserId,
        )
    }
}
