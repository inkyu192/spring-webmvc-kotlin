package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Member

interface MemberRepository {
    fun findByIdOrNull(id: Long): Member?
    fun findByAccount(account: String): Member?
    fun existsByAccount(account: String): Boolean
    fun save(member: Member): Member
    fun delete(member: Member)
}