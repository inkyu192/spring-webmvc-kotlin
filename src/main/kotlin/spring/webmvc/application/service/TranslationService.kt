package spring.webmvc.application.service

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import spring.webmvc.domain.repository.TranslationRepository
import java.text.MessageFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class TranslationService(
    private val translationRepository: TranslationRepository,
) {
    @Volatile
    private var cache: Cache<String, String> = Caffeine.newBuilder().build()

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    fun reloadCache() {
        val newCache = Caffeine.newBuilder().build<String, String>()

        translationRepository.findAll().forEach {
            newCache.put("${it.code}:${it.locale}", it.message)
        }

        cache = newCache
    }

    private fun resolveMessage(code: String, locale: Locale, args: Array<Any>): String? {
        val message = cache.getIfPresent("$code:${locale.language}")
            ?: cache.getIfPresent("$code:${Locale.ENGLISH.language}")
            ?: return null

        return MessageFormat(message, locale).format(args)
    }

    fun getMessage(
        code: String,
        locale: Locale,
        args: Array<Any> = emptyArray(),
    ) = checkNotNull(
        resolveMessage(
            code = code,
            locale = locale,
            args = args,
        )
    )

    fun getMessageOrNull(
        code: String,
        locale: Locale,
        args: Array<Any> = emptyArray(),
    ) = resolveMessage(
        code = code,
        locale = locale,
        args = args,
    )
}
