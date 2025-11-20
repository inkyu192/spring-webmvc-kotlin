package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.vo.Email

interface MemberRepository {
    fun findById(id: Long): Member
    fun findByEmail(email: Email): Member?
    fun existsByEmail(email: Email): Boolean
    fun save(member: Member): Member
    fun delete(member: Member)
}