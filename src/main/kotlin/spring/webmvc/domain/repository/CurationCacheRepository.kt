package spring.webmvc.domain.repository

import spring.webmvc.domain.model.cache.CurationCache
import spring.webmvc.domain.model.cache.CurationProductCache

interface CurationCacheRepository {
    fun setCurations(curations: List<CurationCache>)
    fun getCurations(): List<CurationCache>
    fun setCurationProducts(curationId: Long, cursorId: Long?, size: Int, cache: CurationProductCache)
    fun getCurationProducts(curationId: Long, cursorId: Long?, size: Int): CurationProductCache?
    fun delete()
}