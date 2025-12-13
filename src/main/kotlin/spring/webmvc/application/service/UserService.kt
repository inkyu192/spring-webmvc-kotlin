package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.query.UserSearchQuery
import spring.webmvc.application.dto.result.UserDetail
import spring.webmvc.domain.repository.UserCredentialRepository
import spring.webmvc.domain.repository.UserRepository

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val userCredentialRepository: UserCredentialRepository,
) {
    fun findUsers(query: UserSearchQuery) = userRepository.findAll(
        pageable = query.pageable,
        phone = query.phone,
        name = query.name,
        createdFrom = query.createdFrom,
        createdTo = query.createdTo,
    )

    fun findUserDetail(userId: Long): UserDetail {
        val user = userRepository.findById(userId)
        val credential = userCredentialRepository.findByUser(user)

        return UserDetail(
            user = user,
            credential = credential,
            oauths = emptyList(),
        )
    }
}