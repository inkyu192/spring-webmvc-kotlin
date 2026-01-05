package spring.webmvc.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.application.dto.query.UserQuery
import spring.webmvc.application.dto.result.UserCredentialResult
import spring.webmvc.domain.repository.UserCredentialRepository
import spring.webmvc.domain.repository.UserRepository

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val userCredentialRepository: UserCredentialRepository,
) {
    fun findUsers(query: UserQuery) = userRepository.findAllWithOffsetPage(
        pageable = query.pageable,
        phone = query.phone,
        name = query.name,
        createdFrom = query.createdFrom,
        createdTo = query.createdTo,
    )

    fun findUserDetail(id: Long): UserCredentialResult {
        val user = userRepository.findById(id)
        val credential = userCredentialRepository.findByUserId(id)

        return UserCredentialResult(
            user = user,
            credential = credential,
            oauths = emptyList(),
        )
    }
}