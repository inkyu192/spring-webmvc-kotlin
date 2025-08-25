package spring.webmvc.domain.repository.cache

import spring.webmvc.domain.model.cache.AccommodationCache

interface AccommodationCacheRepository {
    fun getAccommodation(productId: Long): AccommodationCache?
    fun setAccommodation(productId: Long, accommodationCache: AccommodationCache)
    fun deleteAccommodation(productId: Long): Boolean
}