package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.query.UserSearchQuery
import spring.webmvc.application.dto.command.UserStatusUpdateCommand
import spring.webmvc.application.dto.command.UserUpdateCommand
import spring.webmvc.domain.model.entity.User
import spring.webmvc.domain.repository.UserRepository

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {
    fun findUsers(query: UserSearchQuery) = userRepository.findAll(
        pageable = query.pageable,
        email = query.email,
        phone = query.phone,
        name = query.name,
        status = query.status,
        createdFrom = query.createdFrom,
        createdTo = query.createdTo,
    )

    fun findUser(userId: Long) = userRepository.findById(userId)

    @Transactional
    fun updateUser(command: UserUpdateCommand): User {
        val user = userRepository.findById(command.userId)

        user.update(
            name = command.name,
            phone = command.phone,
            birthDate = command.birthDate,
        )

        return user
    }

    @Transactional
    fun updateUserStatus(command: UserStatusUpdateCommand): User {
        val user = userRepository.findById(command.id)

        user.updateStatus(command.status)

        return user
    }
}