package spring.webmvc.infrastructure.persistence.adapter

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.Item
import spring.webmvc.domain.repository.ItemRepository
import spring.webmvc.infrastructure.persistence.ItemJpaRepository
import spring.webmvc.infrastructure.persistence.ItemQuerydslRepository

@Component
class ItemRepositoryAdapter(
    private val jpaRepository: ItemJpaRepository,
    private val querydslRepository: ItemQuerydslRepository,
) : ItemRepository {
    override fun findAll(pageable: Pageable, name: String?) =
        querydslRepository.findAll(pageable = pageable, name = name)

    override fun findByIdOrNull(id: Long) = jpaRepository.findByIdOrNull(id)

    override fun save(item: Item) = jpaRepository.save(item)

    override fun saveAll(items: Iterable<Item>): List<Item> = jpaRepository.saveAll(items)

    override fun delete(item: Item) {
        jpaRepository.delete(item)
    }
}