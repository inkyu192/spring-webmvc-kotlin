package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class MemberRole protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    val role: Role,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    companion object {
        fun create(member: Member, role: Role) = MemberRole(member = member, role = role)
    }
}