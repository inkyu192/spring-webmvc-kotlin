package spring.webmvc.infrastructure.persistence.jpa

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.vo.Phone
import spring.webmvc.infrastructure.config.RepositoryTest
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RepositoryTest
class UserKotlinJdslRepositoryTest(
    @Autowired private val entityManager: EntityManager,
    @Autowired private val jpqlRenderContext: JpqlRenderContext,
) {
    private val userKotlinJdslRepository = UserKotlinJdslRepository(entityManager, jpqlRenderContext)

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    @BeforeEach
    fun setUp() {

        user1 = User.create(
            name = "홍길동",
            phone = Phone.create("010-1111-1111"),
            gender = Gender.MALE,
            birthday = LocalDate.of(1990, 1, 1),
        )
        user2 = User.create(
            name = "김철수",
            phone = Phone.create("010-2222-2222"),
            gender = Gender.MALE,
            birthday = LocalDate.of(1985, 5, 15),
        )
        user3 = User.create(
            name = "이영희",
            phone = Phone.create("010-3333-3333"),
            gender = Gender.FEMALE,
            birthday = LocalDate.of(1995, 12, 25),
        )

        entityManager.persist(user1)
        entityManager.persist(user2)
        entityManager.persist(user3)

        entityManager.flush()
        entityManager.clear()
    }

    @Test
    @DisplayName("findAllWithOffsetPage: 전화번호 조건으로 회원을 조회한다")
    fun findAllWithOffsetPageByPhone() {
        val pageable = PageRequest.of(0, 10)
        val createdFrom = Instant.now().minus(1, ChronoUnit.DAYS)
        val createdTo = Instant.now().plus(1, ChronoUnit.DAYS)

        val result = userKotlinJdslRepository.findAllWithOffsetPage(
            pageable = pageable,
            phone = Phone.create("010-1111-1111"),
            name = null,
            createdFrom = createdFrom,
            createdTo = createdTo,
        )

        Assertions.assertThat(result.totalElements).isEqualTo(1)
        Assertions.assertThat(result.content).hasSize(1)
        Assertions.assertThat(result.content[0].phone.value).isEqualTo("010-1111-1111")
    }

    @Test
    @DisplayName("findAllWithOffsetPage: 조건 없이 전체 회원을 페이징 조회한다")
    fun findAllWithOffsetPageWithoutCondition() {
        val pageable = PageRequest.of(0, 2)
        val createdFrom = Instant.now().minus(1, ChronoUnit.DAYS)
        val createdTo = Instant.now().plus(1, ChronoUnit.DAYS)

        val result = userKotlinJdslRepository.findAllWithOffsetPage(
            pageable = pageable,
            phone = null,
            name = null,
            createdFrom = createdFrom,
            createdTo = createdTo,
        )

        Assertions.assertThat(result.totalElements).isEqualTo(3)
        Assertions.assertThat(result.content).hasSize(2)
        Assertions.assertThat(result.totalPages).isEqualTo(2)
    }
}