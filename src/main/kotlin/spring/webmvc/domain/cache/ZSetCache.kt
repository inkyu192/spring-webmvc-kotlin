package spring.webmvc.domain.cache

import java.time.Duration

interface ZSetCache {
    fun <T> add(key: String, value: T, score: Double)
    fun expire(key: String, timeout: Duration): Boolean
    fun size(key: String): Long
    fun <T> range(key: String, start: Long, end: Long, clazz: Class<T>): Set<T>
}