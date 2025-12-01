package spring.webmvc.application.dto.query

import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

data class MemberSearchQuery(
    val pageable: Pageable,
    val email: Email?,
    val phone: Phone?,
    val name: String?,
    val status: MemberStatus?,
    val createdFrom: Instant,
    val createdTo: Instant,
)