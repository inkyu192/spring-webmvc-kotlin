package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.QMember.member
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

@Repository
class MemberQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findAll(
        pageable: Pageable,
        email: Email?,
        phone: Phone?,
        name: String?,
        status: MemberStatus?,
        createdFrom: Instant,
        createdTo: Instant,
    ): Page<Member> {
        val total = jpaQueryFactory
            .select(member.count())
            .from(member)
            .where(
                eqEmail(email),
                eqPhone(phone),
                eqName(name),
                eqStatus(status),
                betweenCreatedAt(createdFrom, createdTo),
            )
            .fetchOne() ?: 0L

        val content = jpaQueryFactory
            .selectFrom(member)
            .where(
                eqEmail(email),
                eqPhone(phone),
                eqName(name),
                eqStatus(status),
                betweenCreatedAt(createdFrom, createdTo),
            )
            .orderBy(member.id.desc())
            .limit(pageable.pageSize.toLong())
            .offset(pageable.offset)
            .fetch()

        return PageImpl(content, pageable, total)
    }

    private fun eqEmail(email: Email?) = email?.let { member.email.eq(it) }

    private fun eqPhone(phone: Phone?) = phone?.let { member.phone.eq(it) }

    private fun eqName(name: String?) = name?.let { member.name.eq(it) }

    private fun eqStatus(status: MemberStatus?) = status?.let { member.status.eq(it) }

    private fun betweenCreatedAt(from: Instant, to: Instant) = member.createdAt.between(from, to)
}