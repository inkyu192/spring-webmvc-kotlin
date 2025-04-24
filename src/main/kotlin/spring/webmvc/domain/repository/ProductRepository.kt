package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.Product

interface ProductRepository {
    fun findAll(pageable: Pageable, name: String?): Page<Product>
    fun findAllById(ids: Iterable<Long>): List<Product>
}