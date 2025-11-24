package spring.webmvc.domain.service

import org.springframework.stereotype.Service
import spring.webmvc.domain.dto.command.MemberCreateCommand
import spring.webmvc.domain.model.entity.Member
import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.domain.model.entity.Role

@Service
class MemberDomainService {
    fun createMember(
        command: MemberCreateCommand,
        roles: List<Role>,
        permissions: List<Permission>,
    ): Member {
        val member = Member.create(
            email = command.email,
            password = command.password,
            name = command.name,
            phone = command.phone,
            birthDate = command.birthDate,
            type = command.memberType,
        )

        roles.forEach { member.addRole(it) }
        permissions.forEach { member.addPermission(it) }

        return member
    }
}
