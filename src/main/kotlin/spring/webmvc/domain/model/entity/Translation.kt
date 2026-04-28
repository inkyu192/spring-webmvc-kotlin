package spring.webmvc.domain.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Translation protected constructor(
    val code: String,

    val locale: String,

    val message: String,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set

    companion object {
        fun create(
            code: String,
            locale: String,
            message: String,
        ) = Translation(
            code = code,
            locale = locale,
            message = message,
        )
    }
}
