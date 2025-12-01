package spring.webmvc.infrastructure.cache.redis

import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.repository.cache.AuthCacheRepository
import java.time.Duration

@Repository
class AuthRedisRepository(
    private val redisTemplate: RedisTemplate<String, String>,
) : AuthCacheRepository {
    private val logger = LoggerFactory.getLogger(AuthRedisRepository::class.java)

    companion object {
        private const val JOIN_VERIFY_KEY = "auth:join-verify:%s"
        private const val PASSWORD_RESET_KEY = "auth:password-reset:%s"
        private val JOIN_VERIFY_TTL = Duration.ofMinutes(5)
        private val PASSWORD_RESET_TTL = Duration.ofMinutes(5)
    }

    override fun setJoinVerifyToken(token: String, email: Email) {
        val key = JOIN_VERIFY_KEY.format(token)
        runCatching {
            redisTemplate.opsForValue().set(key, email.value, JOIN_VERIFY_TTL)
        }.onFailure {
            logger.error("Failed to save join verify token email={}: {}", email.value, it.message, it)
        }
    }

    override fun getJoinVerifyToken(token: String): String? {
        val key = JOIN_VERIFY_KEY.format(token)
        return runCatching {
            redisTemplate.opsForValue().get(key)
        }.onFailure {
            logger.warn("Failed to get join verify token token={}: {}", token, it.message)
        }.getOrNull()
    }

    override fun deleteJoinVerifyToken(token: String) {
        val key = JOIN_VERIFY_KEY.format(token)
        runCatching {
            redisTemplate.delete(key)
        }.onFailure {
            logger.warn("Failed to delete join verify token token={}: {}", token, it.message)
        }
    }

    override fun setPasswordResetToken(token: String, email: Email) {
        val key = PASSWORD_RESET_KEY.format(token)
        runCatching {
            redisTemplate.opsForValue().set(key, email.value, PASSWORD_RESET_TTL)
        }.onFailure {
            logger.error("Failed to save password reset token email={}: {}", email.value, it.message, it)
        }
    }

    override fun getPasswordResetToken(token: String): String? {
        val key = PASSWORD_RESET_KEY.format(token)
        return runCatching {
            redisTemplate.opsForValue().get(key)
        }.onFailure {
            logger.warn("Failed to get password reset token token={}: {}", token, it.message)
        }.getOrNull()
    }

    override fun deletePasswordResetToken(token: String) {
        val key = PASSWORD_RESET_KEY.format(token)
        runCatching {
            redisTemplate.delete(key)
        }.onFailure {
            logger.warn("Failed to delete password reset token token={}: {}", token, it.message)
        }
    }
}
