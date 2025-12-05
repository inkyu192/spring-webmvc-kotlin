package spring.webmvc.infrastructure.persistence.jpa

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import io.mockk.every
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.enums.MemberType
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.infrastructure.config.RepositoryTest
import spring.webmvc.infrastructure.crypto.CryptoService
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RepositoryTest
class MemberKotlinJdslRepositoryTest(
    @Autowired private val entityManager: EntityManager,
    @Autowired private val jpqlRenderContext: JpqlRenderContext,
    @Autowired private val cryptoService: CryptoService,
) {
    private val memberKotlinJdslRepository = MemberKotlinJdslRepository(entityManager, jpqlRenderContext)

    private lateinit var member1: Member
    private lateinit var member2: Member
    private lateinit var member3: Member

    @BeforeEach
    fun setUp() {
        every { cryptoService.encrypt(any()) } answers { firstArg() }
        every { cryptoService.decrypt(any()) } answers { firstArg() }

        member1 = Member.create(
            email = Email.create("test1@example.com"),
            password = "password123",
            name = "홍길동",
            phone = Phone.create("010-1111-1111"),
            birthDate = LocalDate.of(1990, 1, 1),
            type = MemberType.CUSTOMER,
        )
        member2 = Member.create(
            email = Email.create("test2@example.com"),
            password = "password123",
            name = "김철수",
            phone = Phone.create("010-2222-2222"),
            birthDate = LocalDate.of(1985, 5, 15),
            type = MemberType.CUSTOMER,
        )
        member3 = Member.create(
            email = Email.create("test3@example.com"),
            password = "password123",
            name = "이영희",
            phone = Phone.create("010-3333-3333"),
            birthDate = LocalDate.of(1995, 12, 25),
            type = MemberType.PARTNER,
        )

        entityManager.persist(member1)
        entityManager.persist(member2)
        entityManager.persist(member3)

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    @DisplayName("findAll: 이메일 조건으로 회원을 조회한다")
    fun findAllByEmail() {
        val pageable = PageRequest.of(0, 10)
        val createdFrom = Instant.now().minus(1, ChronoUnit.DAYS)
        val createdTo = Instant.now().plus(1, ChronoUnit.DAYS)

        val result = memberKotlinJdslRepository.findAll(
            pageable = pageable,
            email = Email.create("test1@example.com"),
            phone = null,
            name = null,
            status = null,
            createdFrom = createdFrom,
            createdTo = createdTo,
        )

        Assertions.assertThat(result.totalElements).isEqualTo(1)
        Assertions.assertThat(result.content).hasSize(1)
        Assertions.assertThat(result.content[0].email.value).isEqualTo("test1@example.com")
    }

    @Test
    @DisplayName("findAll: 조건 없이 전체 회원을 페이징 조회한다")
    fun findAllWithoutCondition() {
        val pageable = PageRequest.of(0, 2)
        val createdFrom = Instant.now().minus(1, ChronoUnit.DAYS)
        val createdTo = Instant.now().plus(1, ChronoUnit.DAYS)

        val result = memberKotlinJdslRepository.findAll(
            pageable = pageable,
            email = null,
            phone = null,
            name = null,
            status = null,
            createdFrom = createdFrom,
            createdTo = createdTo,
        )

        Assertions.assertThat(result.totalElements).isEqualTo(3)
        Assertions.assertThat(result.content).hasSize(2)
        Assertions.assertThat(result.totalPages).isEqualTo(2)
    }
}