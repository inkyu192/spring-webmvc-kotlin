package spring.webmvc.infrastructure.persistence.jpa

import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.enums.UserStatus
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

@Repository
class UserKotlinJdslRepository(
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
) {
    fun findAll(
        pageable: Pageable,
        email: Email?,
        phone: Phone?,
        name: String?,
        status: UserStatus?,
        createdFrom: Instant,
        createdTo: Instant,
    ): Page<User> {
        val total = entityManager.createQuery(
            query = jpql {
                select(count(entity(User::class)))
                    .from(entity(User::class))
                    .whereAnd(
                        eqEmail(email),
                        eqPhone(phone),
                        eqName(name),
                        eqStatus(status),
                        betweenCreatedAt(createdFrom, createdTo),
                    )
            }, context = context
        ).singleResult

        val content = entityManager.createQuery(
            query = jpql {
                select(entity(User::class))
                    .from(entity(User::class))
                    .whereAnd(
                        eqEmail(email),
                        eqPhone(phone),
                        eqName(name),
                        eqStatus(status),
                        betweenCreatedAt(createdFrom, createdTo),
                    )
                    .orderBy(path(User::id).desc())
            }, context = context
        )
            .setFirstResult(pageable.offset.toInt())
            .setMaxResults(pageable.pageSize)
            .resultList

        return PageImpl(content, pageable, total)
    }

    private fun Jpql.eqEmail(email: Email?): Predicate? =
        email?.let { path(User::email).eq(it) }

    private fun Jpql.eqPhone(phone: Phone?): Predicate? =
        phone?.let { path(User::phone).eq(it) }

    private fun Jpql.eqName(name: String?): Predicate? =
        name?.let { path(User::name).eq(it) }

    private fun Jpql.eqStatus(status: UserStatus?): Predicate? =
        status?.let { path(User::status).eq(it) }

    private fun Jpql.betweenCreatedAt(from: Instant, to: Instant): Predicate =
        path(User::createdAt).between(from, to)
}
