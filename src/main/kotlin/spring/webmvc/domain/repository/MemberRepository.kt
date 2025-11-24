package spring.webmvc.domain.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.enums.MemberStatus
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.Instant

interface MemberRepository {
    fun findById(id: Long): Member
    fun findAll(
        pageable: Pageable,
        email: Email?,
        phone: Phone?,
        name: String?,
        status: MemberStatus?,
        createdFrom: Instant,
        createdTo: Instant,
    ): Page<Member>
    fun findByEmail(email: Email): Member?
    fun existsByEmail(email: Email): Boolean
    fun save(member: Member): Member
}