package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.UserCredentialRepository
import spring.webmvc.infrastructure.persistence.jpa.UserCredentialJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.UserCredentialQuerydslRepository

@Component
class UserCredentialRepositoryAdapter(
    private val jpaRepository: UserCredentialJpaRepository,
    private val querydslRepository: UserCredentialQuerydslRepository,
) : UserCredentialRepository {
    override fun findByEmail(email: Email) = jpaRepository.findByEmailValue(email.value)

    override fun findByUserId(userId: Long) = querydslRepository.findByUserId(userId)

    override fun existsByEmail(email: Email) = jpaRepository.existsByEmailValue(email.value)

    override fun save(userCredential: UserCredential): UserCredential = jpaRepository.save(userCredential)
}
