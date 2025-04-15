package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class MemberPermission protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var permission: Permission,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var member: Member,
) {
    @Id
    @GeneratedValue
    @Column(name = "member_permission_id")
    var id: Long? = null
        protected set

    companion object {
        fun create(member: Member, permission: Permission) = MemberPermission(member = member, permission = permission)
    }
}