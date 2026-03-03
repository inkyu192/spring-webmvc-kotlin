package spring.webmvc.infrastructure.security

import org.springframework.security.core.context.SecurityContextHolder
import spring.webmvc.infrastructure.exception.InvalidCredentialsException

object SecurityContextUtil {
    fun getUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw InvalidCredentialsException()

        if (!authentication.isAuthenticated) {
            throw InvalidCredentialsException()
        }

        return authentication.principal.toString().toLongOrNull()
            ?: throw InvalidCredentialsException()
    }

    fun getUserIdOrNull(): Long? {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated) {
            return null
        }

        return authentication.principal.toString().toLongOrNull()
    }

    fun getAuthorities(): Set<String> {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw InvalidCredentialsException()

        if (!authentication.isAuthenticated) {
            throw InvalidCredentialsException()
        }

        return authentication.authorities.map { it.authority }.toSet()
    }
}
