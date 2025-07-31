package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.infrastructure.persistence.jpa.MemberJpaRepository

@Component
class MemberRepositoryAdapter(
    private val jpaRepository: MemberJpaRepository
) : MemberRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun findByEmail(email: Email) = jpaRepository.findByEmail(email)

    override fun existsByEmail(email: Email) = jpaRepository.existsByEmail(email)

    override fun save(member: Member) = jpaRepository.save(member)

    override fun delete(member: Member) {
        jpaRepository.delete(member)
    }
}