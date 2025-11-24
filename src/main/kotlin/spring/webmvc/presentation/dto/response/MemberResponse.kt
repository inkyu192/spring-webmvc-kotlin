package spring.webmvc.presentation.dto.response

import org.springframework.data.domain.Page
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
    companion object {
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                id = checkNotNull(member.id),
                email = member.email.value,
                name = member.name,
                phone = member.phone.value,
                birthDate = member.birthDate,
                createdAt = member.createdAt,
            )
        }
    }
}

data class MemberPageResponse(
    val page: OffsetPageResponse,
    val members: List<MemberResponse>,
) {
    companion object {
        fun from(page: Page<Member>) = MemberPageResponse(
            page = OffsetPageResponse(page),
            members = page.content.map { MemberResponse.from(member = it) },
        )
    }
}
