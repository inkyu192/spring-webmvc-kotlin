package spring.webmvc.domain.cache

import java.time.Duration

interface KeyValueCache {
    fun get(key: String): String?
    fun set(key: String, value: String, timeout: Duration?)
    fun setIfAbsent(key: String, value: String, timeout: Duration?): Boolean
}