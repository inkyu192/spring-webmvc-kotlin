package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.QUser.user
import spring.webmvc.domain.model.entity.QUserCredential.userCredential

@Repository
class UserCredentialQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findByUserId(userId: Long) = jpaQueryFactory
        .selectFrom(userCredential)
        .join(userCredential.user, user).fetchJoin()
        .where(
            eqUserId(userId)
        )
        .fetchOne()

    private fun eqUserId(userId: Long) = userCredential.user.id.eq(userId)
}
