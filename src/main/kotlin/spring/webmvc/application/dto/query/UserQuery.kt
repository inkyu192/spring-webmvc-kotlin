package spring.webmvc.application.dto.query

import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.enums.UserStatus
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

data class UserSearchQuery(
    val pageable: Pageable,
    val email: Email?,
    val phone: Phone?,
    val name: String?,
    val status: UserStatus?,
    val createdFrom: Instant,
    val createdTo: Instant,
) {
    companion object {
        fun create(
            pageable: Pageable,
            email: String?,
            phone: String?,
            name: String?,
            status: UserStatus?,
            createdFrom: Instant,
            createdTo: Instant,
        ) = UserSearchQuery(
            pageable = pageable,
            email = email?.let { Email.create(it) },
            phone = phone?.let { Phone.create(it) },
            name = name,
            status = status,
            createdFrom = createdFrom,
            createdTo = createdTo,
        )
    }
}
