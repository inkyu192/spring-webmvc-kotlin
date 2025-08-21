package spring.webmvc.domain.cache

import java.time.Duration

interface ValueCache {
    fun <T> get(key: String, clazz: Class<T>): T?
    fun <T> set(key: String, value: T, timeout: Duration? = null)
    fun delete(key: String): Boolean
    fun increment(key: String, delta: Long): Long?
    fun decrement(key: String, delta: Long): Long?
}