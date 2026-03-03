package spring.webmvc.domain.repository.cache

import java.util.*

interface TranslationCacheRepository {
    fun getMessage(code: String, locale: Locale, args: Array<Any> = emptyArray()): String
    fun getMessageOrNull(code: String, locale: Locale, args: Array<Any> = emptyArray()): String?
}
