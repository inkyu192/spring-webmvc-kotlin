package spring.webmvc.infrastructure.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.cache.CurationCache
import spring.webmvc.domain.model.cache.CurationProductCache
import spring.webmvc.domain.repository.CurationCacheRepository
import java.time.Duration

@Repository
class CurationRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : CurationCacheRepository {
    companion object {
        private const val CURATIONS_KEY = "curations"
    }

    override fun setCurations(curations: List<CurationCache>) {
        val jsonValue = objectMapper.writeValueAsString(curations)

        redisTemplate.opsForValue().set(CURATIONS_KEY, jsonValue, Duration.ofHours(1))
    }

    override fun getCurations(): List<CurationCache> {
        val jsonValue = redisTemplate.opsForValue().get(CURATIONS_KEY)

        return jsonValue?.let {
            try {
                objectMapper.readValue(
                    it,
                    objectMapper.typeFactory.constructCollectionType(
                        List::class.java,
                        CurationProductCache::class.java
                    )
                )
            } catch (e: Exception) {
                emptyList()
            }
        } ?: emptyList()
    }

    override fun setCurationProducts(
        curationId: Long,
        cursorId: Long?,
        size: Int,
        cache: CurationProductCache,
    ) {
        val key = "$CURATIONS_KEY:$curationId:cursor:${cursorId ?: "null"}:size:$size"
        val jsonValue = objectMapper.writeValueAsString(cache)
        redisTemplate.opsForValue().set(key, jsonValue, Duration.ofHours(1))
    }

    override fun getCurationProducts(curationId: Long, cursorId: Long?, size: Int): CurationProductCache? {
        val key = "$CURATIONS_KEY:$curationId:cursor:${cursorId ?: "null"}:size:$size"
        val jsonValue = redisTemplate.opsForValue().get(key)

        return jsonValue?.let {
            try {
                objectMapper.readValue(it, CurationProductCache::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun deleteAll() {
        val keys = redisTemplate.keys("$CURATIONS_KEY*")

        redisTemplate.delete(keys)
    }
}