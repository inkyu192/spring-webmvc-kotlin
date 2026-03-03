package spring.webmvc.domain.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "translation")
class Translation(
    val code: String,

    val locale: String,

    val message: String,
) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        protected set
}
