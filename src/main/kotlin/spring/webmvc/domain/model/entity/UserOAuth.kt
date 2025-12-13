package spring.webmvc.domain.model.entity

import jakarta.persistence.*
import spring.webmvc.domain.model.enums.OAuthProvider

@Entity
@Table(name = "user_oauth")
class UserOAuth protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider")
    val oauthProvider: OAuthProvider,

    @Column(name = "oauth_user_id")
    val oauthUserId: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(
            user: User,
            oauthProvider: OAuthProvider,
            oauthUserId: String,
        ) = UserOAuth(
            user = user,
            oauthProvider = oauthProvider,
            oauthUserId = oauthUserId,
        )
    }
}