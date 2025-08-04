package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.vo.Email

interface MemberJpaRepository : JpaRepository<Member, Long> {

    fun findByEmail(email: Email): Member?

    fun existsByEmail(email: Email): Boolean
}