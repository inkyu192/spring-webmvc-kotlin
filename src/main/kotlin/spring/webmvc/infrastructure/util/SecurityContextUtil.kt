package spring.webmvc.infrastructure.util

import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder

object SecurityContextUtil {
    fun getMemberId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BadCredentialsException("인증 정보가 없습니다.")

        if (!authentication.isAuthenticated) {
            throw BadCredentialsException("인증되지 않은 사용자입니다.")
        }

        return authentication.principal.toString().toLongOrNull()
            ?: throw BadCredentialsException("잘못된 인증 정보입니다.")
    }

    fun getMemberIdOrNull(): Long? {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated) {
            return null
        }

        return authentication.principal.toString().toLongOrNull()
    }
}