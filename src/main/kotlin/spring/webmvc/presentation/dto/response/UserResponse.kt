package spring.webmvc.presentation.dto.response

import org.springframework.data.domain.Page
import spring.webmvc.domain.model.entity.User
import java.time.Instant
import java.time.LocalDate

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val phone: String,
    val birthDate: LocalDate,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = checkNotNull(user.id),
                email = user.email.value,
                name = user.name,
                phone = user.phone.value,
                birthDate = user.birthDate,
                createdAt = user.createdAt,
            )
        }
    }
}

data class UserPageResponse(
    val page: OffsetPageResponse,
    val users: List<UserResponse>,
) {
    companion object {
        fun from(page: Page<User>) = UserPageResponse(
            page = OffsetPageResponse(page),
            users = page.content.map { UserResponse.from(user = it) },
        )
    }
}
