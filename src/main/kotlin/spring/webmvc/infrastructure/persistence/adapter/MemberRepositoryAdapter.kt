package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.domain.repository.MemberRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.MemberJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.MemberQuerydslRepository
import java.time.Instant

@Component
class MemberRepositoryAdapter(
    private val jpaRepository: MemberJpaRepository,
    private val querydslRepository: MemberQuerydslRepository,
) : MemberRepository {
    override fun findById(id: Long): Member = jpaRepository.findByIdOrThrow(id)

    override fun findAll(
        pageable: Pageable,
        email: Email?,
        phone: Phone?,
        name: String?,
        status: MemberStatus?,
        createdFrom: Instant,
        createdTo: Instant,
    ) = querydslRepository.findAll(
        pageable = pageable,
        email = email,
        phone = phone,
        name = name,
        status = status,
        createdFrom = createdFrom,
        createdTo = createdTo,
    )

    override fun findByEmail(email: Email) = jpaRepository.findByEmail(email)

    override fun existsByEmail(email: Email) = jpaRepository.existsByEmail(email)

    override fun save(member: Member) = jpaRepository.save(member)
}