package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

interface UserRepository {
    fun findById(id: Long): User
    fun findAll(
        pageable: Pageable,
        phone: Phone?,
        name: String?,
        createdFrom: Instant,
        createdTo: Instant,
    ): Page<User>
    fun save(user: User): User
    fun existsByPhone(phone: Phone): Boolean
}
