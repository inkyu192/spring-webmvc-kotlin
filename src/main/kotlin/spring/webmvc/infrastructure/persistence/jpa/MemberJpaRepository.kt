package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Member

interface MemberJpaRepository : JpaRepository<Member, Long> {

    fun findByAccount(account: String): Member?

    fun existsByAccount(account: String): Boolean
}