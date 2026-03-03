package spring.webmvc.infrastructure.exception

import org.springframework.security.core.AuthenticationException

class InvalidCredentialsException : AuthenticationException(null)