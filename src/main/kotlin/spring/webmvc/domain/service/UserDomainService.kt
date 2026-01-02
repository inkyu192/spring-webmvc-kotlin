package spring.webmvc.domain.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import spring.webmvc.domain.model.entity.*
import spring.webmvc.domain.model.enums.Gender
import spring.webmvc.domain.model.vo.Email
import spring.webmvc.domain.model.vo.Phone
import java.time.LocalDate

@Component
class UserDomainService(
    private val passwordEncoder: PasswordEncoder,
) {
    fun createUserWithCredential(
        name: String,
        phone: Phone,
        gender: Gender,
        birthday: LocalDate,
        email: Email,
        password: String,
        roles: List<Role>,
        permissions: List<Permission>,
    ): Pair<User, UserCredential> {
        val user = User.create(
            name = name,
            phone = phone,
            gender = gender,
            birthday = birthday,
        )

        roles.forEach {
            val userRole = UserRole.create(user = user, role = it)
            user.addUserRole(userRole)
        }

        permissions.forEach {
            val userPermission = UserPermission.create(user = user, permission = it)
            user.addUserPermission(userPermission)
        }

        val userCredential = UserCredential.create(
            user = user,
            email = email,
            password = passwordEncoder.encode(password),
        )

        return user to userCredential
    }
}
