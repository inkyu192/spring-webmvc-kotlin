package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Role
import spring.webmvc.domain.repository.RoleRepository
import spring.webmvc.infrastructure.persistence.jpa.RoleJpaRepository

@Component
class RoleRepositoryAdapter(
    private val jpaRepository: RoleJpaRepository
) : RoleRepository {
    override fun save(role: Role) = jpaRepository.save(role)

    override fun saveAll(roles: Iterable<Role>) = jpaRepository.saveAll(roles)

    override fun findAllById(ids: Iterable<Long>): List<Role> = jpaRepository.findAllById(ids)
}