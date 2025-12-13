package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.infrastructure.persistence.jpa.RoleJpaRepository

@Component
class RoleRepositoryAdapter(
    private val jpaRepository: RoleJpaRepository,
) : RoleRepository {
    override fun findAllById(ids: Iterable<Long>) = jpaRepository.findAllById(ids)
}