package spring.webmvc.application.dto.query

import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.enums.MemberStatus
import java.time.Instant

data class MemberSearchQuery(
    val pageable: Pageable,
    val email: String?,
    val phone: String?,
    val name: String?,
    val status: MemberStatus?,
    val createdFrom: Instant,
    val createdTo: Instant,
)