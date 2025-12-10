package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class UserRole protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    val role: Role,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(user: User, role: Role) = UserRole(user = user, role = role)
    }
}
