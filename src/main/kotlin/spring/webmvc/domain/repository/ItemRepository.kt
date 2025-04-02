package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.Item

interface ItemRepository {
    fun findAll(pageable: Pageable, name: String?): Page<Item>
    fun findByIdOrNull(id: Long): Item?
    fun save(item: Item): Item
    fun saveAll(items: Iterable<Item>): List<Item>
    fun delete(item: Item)
}