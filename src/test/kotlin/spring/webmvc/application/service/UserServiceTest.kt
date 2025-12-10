package spring.webmvc.application.service

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spring.webmvc.application.dto.query.UserSearchQuery
import spring.webmvc.application.dto.command.UserCreateCommand
import spring.webmvc.application.dto.command.UserStatusUpdateCommand
import spring.webmvc.application.dto.command.UserUpdateCommand
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.enums.UserStatus
import spring.webmvc.domain.model.enums.UserType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.domain.repository.UserRepository
import java.time.Instant
import java.time.LocalDate

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val userService = UserService(
        userRepository = userRepository,
    )

    private lateinit var user: User
    private lateinit var email: Email
    private lateinit var phone: Phone
    private lateinit var command: UserCreateCommand
    private lateinit var roleIds: List<Long>
    private lateinit var permissionIds: List<Long>

    @BeforeEach
    fun setUp() {
        email = Email.create("test@example.com")
        phone = Phone.create("010-1234-5678")
        roleIds = listOf(1L)
        permissionIds = listOf(1L)
        user = User.create(
            email = email,
            password = "encodedPassword",
            name = "홍길동",
            phone = phone,
            birthDate = LocalDate.of(1990, 1, 1),
            type = UserType.CUSTOMER,
        )
        command = UserCreateCommand(
            email = email,
            password = "password123",
            name = "홍길동",
            phone = phone,
            birthDate = LocalDate.of(1990, 1, 1),
            type = UserType.CUSTOMER,
            roleIds = roleIds,
            permissionIds = permissionIds,
        )
    }

    @Test
    @DisplayName("회원 목록 조회")
    fun findUsers() {
        val pageable = PageRequest.of(0, 10)
        val createdFrom = Instant.now()
        val createdTo = Instant.now()
        val query = UserSearchQuery(
            pageable = pageable,
            email = email,
            phone = null,
            name = null,
            status = null,
            createdFrom = createdFrom,
            createdTo = createdTo,
        )
        val page = PageImpl(listOf(user))

        every { userRepository.findAll(pageable, email, null, null, null, createdFrom, createdTo) } returns page

        val result = userService.findUsers(query)

        Assertions.assertThat(result).isEqualTo(page)
    }

    @Test
    @DisplayName("회원 단건 조회")
    fun findUser() {
        val userId = 1L

        every { userRepository.findById(userId) } returns user

        val result = userService.findUser(userId)

        Assertions.assertThat(result).isEqualTo(user)
    }

    @Test
    @DisplayName("회원 정보 수정")
    fun updateUser() {
        val command = UserUpdateCommand(
            userId = 1L,
            name = "김철수",
            phone = Phone.create("010-9876-5432"),
            birthDate = LocalDate.of(1995, 5, 15),
        )

        every { userRepository.findById(command.userId) } returns user

        val result = userService.updateUser(command)

        Assertions.assertThat(result).isEqualTo(user)
        Assertions.assertThat(result.name).isEqualTo("김철수")
    }

    @Test
    @DisplayName("회원 상태 수정")
    fun updateUserStatus() {
        val command = UserStatusUpdateCommand(
            id = 1L,
            status = UserStatus.SUSPENDED,
        )

        every { userRepository.findById(command.id) } returns user

        val result = userService.updateUserStatus(command)

        Assertions.assertThat(result).isEqualTo(user)
        Assertions.assertThat(result.status).isEqualTo(UserStatus.SUSPENDED)
    }
}
