package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.domain.repository.UserRepository
import spring.webmvc.infrastructure.extensions.findByIdOrThrow
import spring.webmvc.infrastructure.persistence.jpa.UserJpaRepository
import spring.webmvc.infrastructure.persistence.jpa.UserKotlinJdslRepository
import spring.webmvc.infrastructure.persistence.jpa.UserQuerydslRepository
import java.time.Instant

@Component
class UserRepositoryAdapter(
    private val jpaRepository: UserJpaRepository,
    private val querydslRepository: UserQuerydslRepository,
    private val kotlinJdslRepository: UserKotlinJdslRepository,
) : UserRepository {
    override fun findById(id: Long): User = jpaRepository.findByIdOrThrow(id)

    override fun findAll(
        pageable: Pageable,
        phone: Phone?,
        name: String?,
        createdFrom: Instant,
        createdTo: Instant,
    ) = kotlinJdslRepository.findAll(
        pageable = pageable,
        phone = phone,
        name = name,
        createdFrom = createdFrom,
        createdTo = createdTo,
    )

    override fun save(user: User) = jpaRepository.save(user)

    override fun existsByPhone(phone: Phone) = jpaRepository.existsByPhone(phone)
}
