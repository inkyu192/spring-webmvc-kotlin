package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
class UserCompany protected constructor(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    val company: Company,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(user: User, company: Company) = UserCompany(user = user, company = company)
    }
}