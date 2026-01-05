package spring.webmvc.application.dto.query

import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

data class UserQuery(
    val pageable: Pageable,
    val phone: Phone?,
    val name: String?,
    val createdFrom: Instant,
    val createdTo: Instant,
) {
    companion object {
        fun create(
            pageable: Pageable,
            phone: String?,
            name: String?,
            createdFrom: Instant,
            createdTo: Instant,
        ) = UserQuery(
            pageable = pageable,
            phone = phone?.let { Phone.create(it) },
            name = name,
            createdFrom = createdFrom,
            createdTo = createdTo,
        )
    }
}