package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.QUser.user
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

@Repository
class UserQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAllWithOffsetPage(
        pageable: Pageable,
        phone: Phone?,
        name: String?,
        createdFrom: Instant,
        createdTo: Instant,
    ): Page<User> {
        val total = jpaQueryFactory
            .select(user.count())
            .from(user)
            .where(
                eqPhone(phone),
                eqName(name),
                betweenCreatedAt(createdFrom, createdTo),
            )
            .fetchOne() ?: 0L

        val content = jpaQueryFactory
            .selectFrom(user)
            .where(
                eqPhone(phone),
                eqName(name),
                betweenCreatedAt(createdFrom, createdTo),
            )
            .orderBy(user.id.desc())
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        return PageImpl(content, pageable, total)
    }

    private fun eqPhone(phone: Phone?) = phone?.let { user.phone.eq(it) }

    private fun eqName(name: String?) = name?.let { user.name.eq(it) }

    private fun betweenCreatedAt(from: Instant, to: Instant) = user.createdAt.between(from, to)
}
