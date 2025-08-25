package spring.webmvc.infrastructure.cache.redis

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.cache.CurationCache
import spring.webmvc.domain.model.cache.CurationProductCache
import spring.webmvc.domain.repository.cache.CurationCacheRepository
import java.time.Duration

@Repository
class CurationRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) : CurationCacheRepository {
    private val logger = LoggerFactory.getLogger(CurationRedisRepository::class.java)

    companion object {
        private const val CURATION_KEY = "curations"
    }

    override fun setCurations(curations: List<CurationCache>) {
        runCatching {
            val jsonValue = objectMapper.writeValueAsString(curations)
            redisTemplate.opsForValue().set(CURATION_KEY, jsonValue, Duration.ofHours(1))
        }.onFailure { logger.error("Failed to set curations cache for key={}: {}", CURATION_KEY, it.message, it) }
    }

    override fun getCurations(): List<CurationCache> {
        return runCatching {
            val jsonValue = redisTemplate.opsForValue().get(CURATION_KEY)
            jsonValue?.let {
                objectMapper.readValue(
                    it,
                    objectMapper.typeFactory.constructCollectionType(
                        List::class.java,
                        CurationCache::class.java
                    )
                )
            } ?: emptyList<CurationCache>()
        }.onFailure {
            logger.warn("Failed to get curations cache for key={}: {}", CURATION_KEY, it.message)
        }.getOrElse { emptyList() }
    }

    override fun setCurationProducts(
        curationId: Long,
        cursorId: Long?,
        size: Int,
        cache: CurationProductCache,
    ) {
        val key = "$CURATION_KEY:$curationId:cursor:${cursorId ?: "null"}:size:$size"

        runCatching {
            val jsonValue = objectMapper.writeValueAsString(cache)
            redisTemplate.opsForValue().set(key, jsonValue, Duration.ofHours(1))
        }.onFailure {
            logger.error("Failed to set curation products cache for key={}: {}", key, it.message, it)
        }
    }

    override fun getCurationProducts(curationId: Long, cursorId: Long?, size: Int): CurationProductCache? {
        val key = "$CURATION_KEY:$curationId:cursor:${cursorId ?: "null"}:size:$size"

        return runCatching {
            val jsonValue = redisTemplate.opsForValue().get(key)
            jsonValue?.let {
                objectMapper.readValue(it, CurationProductCache::class.java)
            }
        }.onFailure {
            logger.warn("Failed to get curation products cache for key={}: {}", key, it.message)
        }.getOrElse { null }
    }

    override fun deleteAll() {
        runCatching {
            val keys = redisTemplate.keys("$CURATION_KEY*")
            redisTemplate.delete(keys)
        }.onFailure {
            logger.error("Failed to delete all curations cache: {}", it.message, it)
        }
    }
}