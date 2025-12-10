package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.enums.UserStatus
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

interface UserRepository {
    fun findById(id: Long): User
    fun findAll(
        pageable: Pageable,
        email: Email?,
        phone: Phone?,
        name: String?,
        status: UserStatus?,
        createdFrom: Instant,
        createdTo: Instant,
    ): Page<User>
    fun findByEmail(email: Email): User?
    fun existsByEmail(email: Email): Boolean
    fun save(user: User): User
}
