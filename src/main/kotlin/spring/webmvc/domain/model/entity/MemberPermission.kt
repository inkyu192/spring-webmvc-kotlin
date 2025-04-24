package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class MemberPermission protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val permission: Permission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    val member: Member,
) {
    @Id
    @GeneratedValue
    var id: Long? = null
        protected set

    companion object {
        fun create(member: Member, permission: Permission) = MemberPermission(member = member, permission = permission)
    }
}