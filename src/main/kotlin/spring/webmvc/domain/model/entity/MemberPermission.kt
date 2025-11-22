package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class MemberPermission protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    val permission: Permission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(member: Member, permission: Permission) = MemberPermission(member = member, permission = permission)
    }
}