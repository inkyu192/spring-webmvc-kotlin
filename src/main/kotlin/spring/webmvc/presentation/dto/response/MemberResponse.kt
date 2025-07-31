package spring.webmvc.presentation.dto.response

import spring.webmvc.domain.model.entity.Member
import java.time.Instant
import java.time.LocalDate

data class MemberResponse(
    val id: Long,
    val email: String,
    val name: String,
    val phone: String,
    val birthDate: LocalDate,
    val createdAt: Instant,
) {
    constructor(member: Member) : this(
        id = checkNotNull(member.id),
        email = member.email.value,
        name = member.name,
        phone = member.phone.value,
        birthDate = member.birthDate,
        createdAt = member.createdAt,
    )
}
