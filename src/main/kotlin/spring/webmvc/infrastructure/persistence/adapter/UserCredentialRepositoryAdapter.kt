package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.UserCredentialRepository
import spring.webmvc.infrastructure.persistence.jpa.UserCredentialJpaRepository

@Component
class UserCredentialRepositoryAdapter(
    private val jpaRepository: UserCredentialJpaRepository,
) : UserCredentialRepository {
    override fun findByEmail(email: Email) = jpaRepository.findByEmailValue(email.value)

    override fun findByUser(user: User) = jpaRepository.findByUser(user)

    override fun existsByEmail(email: Email) = jpaRepository.existsByEmailValue(email.value)

    override fun save(userCredential: UserCredential): UserCredential = jpaRepository.save(userCredential)
}
