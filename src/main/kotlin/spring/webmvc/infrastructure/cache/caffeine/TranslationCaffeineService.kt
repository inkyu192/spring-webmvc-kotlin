package spring.webmvc.infrastructure.cache.caffeine

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Repository
import spring.webmvc.domain.repository.TranslationRepository
import spring.webmvc.domain.repository.cache.TranslationCacheRepository
import java.text.MessageFormat
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

@Repository
class TranslationCaffeineService(
    private val translationRepository: TranslationRepository,
) : TranslationCacheRepository {
    private val cache: Cache<String, String> = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(1))
        .build()

    @PostConstruct
    fun init() {
        loadTranslations()
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    fun refresh() {
        loadTranslations()
    }

    private fun loadTranslations() {
        cache.invalidateAll()
        translationRepository.findAll().forEach {
            cache.put("${it.code}:${it.locale}", it.message)
        }
    }

    private fun resolveMessage(code: String, locale: Locale, args: Array<Any>): String? {
        val message = cache.getIfPresent("$code:${locale.language}")
            ?: cache.getIfPresent("$code:${Locale.ENGLISH.language}")
            ?: return null

        return MessageFormat(message, locale).format(args)
    }

    override fun getMessage(code: String, locale: Locale, args: Array<Any>): String {
        return resolveMessage(code, locale, args)
            ?: throw IllegalStateException(
                "Translation not found for code '$code' (locale: ${locale.language}, fallback: en)"
            )
    }

    override fun getMessageOrNull(code: String, locale: Locale, args: Array<Any>): String? {
        return resolveMessage(code, locale, args)
    }
}
