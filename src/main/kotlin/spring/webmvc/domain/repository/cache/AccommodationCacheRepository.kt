package spring.webmvc.domain.repository.cache

import spring.webmvc.application.dto.result.AccommodationResult
import java.time.Duration

interface AccommodationCacheRepository {
    fun getAccommodation(productId: Long): AccommodationResult?
    fun setAccommodation(productId: Long, accommodationResult: AccommodationResult, timeout: Duration? = null)
    fun deleteAccommodation(productId: Long): Boolean
}