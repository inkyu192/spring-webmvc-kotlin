package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.infrastructure.persistence.MemberJpaRepository

@Component
class MemberRepositoryAdapter(
    private val jpaRepository: MemberJpaRepository
) : MemberRepository {
    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun findByAccount(account: String) = jpaRepository.findByAccount(account)

    override fun existsByAccount(account: String) = jpaRepository.existsByAccount(account)

    override fun save(member: Member) = jpaRepository.save(member)

    override fun delete(member: Member) {
        jpaRepository.delete(member)
    }
}