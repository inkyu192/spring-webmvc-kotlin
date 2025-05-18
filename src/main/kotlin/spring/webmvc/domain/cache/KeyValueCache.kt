package spring.webmvc.domain.cache

import java.time.Duration

interface KeyValueCache {
    fun get(key: String): String?
    fun set(key: String, value: String, timeout: Duration? = null)
    fun setIfAbsent(key: String, value: String, timeout: Duration? = null): Boolean
    fun delete(key: String): Boolean
    fun increment(key: String, delta: Long): Long?
    fun decrement(key: String, delta: Long): Long?
}