package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Accommodation

interface AccommodationRepository {
    fun findById(id: Long): Accommodation
    fun findByProductId(productId: Long): Accommodation
    fun save(accommodation: Accommodation): Accommodation
    fun delete(accommodation: Accommodation)
}