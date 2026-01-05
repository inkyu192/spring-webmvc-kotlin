package spring.webmvc.infrastructure.extensions

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.findByIdOrNull
import spring.webmvc.infrastructure.exception.NotFoundEntityException

inline fun <reified T, ID : Any> CrudRepository<T, ID>.findByIdOrThrow(id: ID): T =
    findByIdOrNull(id) ?: throw NotFoundEntityException(T::class, id)