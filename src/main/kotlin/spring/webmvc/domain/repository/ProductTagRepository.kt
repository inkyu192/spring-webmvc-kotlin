package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Tag

interface ProductTagRepository {
    fun findTagsByProductId(productId: Long): List<Tag>
}
