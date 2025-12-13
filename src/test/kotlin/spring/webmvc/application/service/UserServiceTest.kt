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
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.entity.UserCredential
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.domain.repository.UserCredentialRepository
import spring.webmvc.domain.repository.UserRepository
import java.time.Instant
import java.time.LocalDate

class UserServiceTest {
    private val userRepository = mockk<UserRepository>()
    private val userCredentialRepository = mockk<UserCredentialRepository>()
    private val userService = UserService(
        userRepository = userRepository,
        userCredentialRepository = userCredentialRepository,
    )

    private lateinit var user: User
    private lateinit var email: Email
    private lateinit var phone: Phone
    private lateinit var userCredential: UserCredential

    @BeforeEach
    fun setUp() {
        email = Email.create("test@example.com")
        phone = Phone.create("010-1234-5678")
        user = User.create(
            name = "홍길동",
            phone = phone,
            gender = Gender.MALE,
            birthday = LocalDate.of(1990, 1, 1),
        )
        userCredential = UserCredential.create(
            user = user,
            email = email,
            password = "encodedPassword",
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
            phone = phone,
            name = "홍길동",
            createdFrom = createdFrom,
            createdTo = createdTo,
        )
        val page = PageImpl(listOf(user))

        every { userRepository.findAll(pageable, phone, "홍길동", createdFrom, createdTo) } returns page

        val result = userService.findUsers(query)

        Assertions.assertThat(result).isEqualTo(page)
    }

    @Test
    @DisplayName("회원 상세 조회")
    fun findUserDetail() {
        val userId = 1L

        every { userRepository.findById(userId) } returns user
        every { userCredentialRepository.findByUser(user) } returns userCredential

        val result = userService.findUserDetail(userId)

        Assertions.assertThat(result.user).isEqualTo(user)
        Assertions.assertThat(result.credential).isEqualTo(userCredential)
    }
}