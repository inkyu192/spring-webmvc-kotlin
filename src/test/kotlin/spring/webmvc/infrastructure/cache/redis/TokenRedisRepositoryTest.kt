package spring.webmvc.infrastructure.cache.redis

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import spring.webmvc.infrastructure.config.CacheTest
import spring.webmvc.infrastructure.properties.AppProperties
import java.time.Duration

@CacheTest
class TokenRedisRepositoryTest(
    @Autowired
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val appProperties = mockk<AppProperties>()
    private val repository: TokenRedisRepository = TokenRedisRepository(redisTemplate, appProperties)

    @Test
    @DisplayName("setRefreshToken: refresh token을 저장한다")
    fun setRefreshToken() {
        val userId = 1L
        val deviceId = "device-1"
        val refreshToken = "test-refresh-token"

        every { appProperties.jwt.refreshToken.expiration } returns Duration.ofSeconds(60)

        repository.setRefreshToken(userId, deviceId, refreshToken)

        val stored = redisTemplate.opsForValue().get("user:${userId}:device:${deviceId}:refresh-token")
        assertThat(stored).isEqualTo(refreshToken)
    }

    @Test
    @DisplayName("getRefreshToken: 저장된 refresh token을 조회한다")
    fun getRefreshToken() {
        val userId = 1L
        val deviceId = "device-1"
        val refreshToken = "test-refresh-token"
        redisTemplate.opsForValue().set("user:${userId}:device:${deviceId}:refresh-token", refreshToken)

        val result = repository.getRefreshToken(userId, deviceId)

        assertThat(result).isEqualTo(refreshToken)
    }

    @Test
    @DisplayName("getRefreshToken: 존재하지 않는 키 조회 시 null을 반환한다")
    fun getRefreshTokenNotFound() {
        val result = repository.getRefreshToken(999L, "non-existent-device")

        assertThat(result).isNull()
    }

    @Test
    @DisplayName("removeRefreshToken: refresh token을 삭제한다")
    fun removeRefreshToken() {
        val userId = 1L
        val deviceId = "device-1"
        val refreshToken = "test-refresh-token"
        redisTemplate.opsForValue().set("user:${userId}:device:${deviceId}:refresh-token", refreshToken)

        repository.removeRefreshToken(userId, deviceId)

        val result = redisTemplate.opsForValue().get("user:${userId}:device:${deviceId}:refresh-token")
        assertThat(result).isNull()
    }
}
