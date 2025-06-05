package spring.webmvc.infrastructure.persistence.mongo

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.context.annotation.Import
import spring.webmvc.domain.model.document.Notification
import spring.webmvc.infrastructure.config.MongoTestContainerConfig

@DataMongoTest
@Import(MongoTestContainerConfig::class)
class NotificationMongoRepositoryTest() {
    @Autowired
    private lateinit var notificationMongoRepository: NotificationMongoRepository

    @Test
    @DisplayName("save: Notification 저장 후 반환한다")
    fun save() {
        val notification = Notification.create(
            memberId = 1L,
            title = "title",
            message = "message",
            url = "url",
        )

        val saved = notificationMongoRepository.save(notification)

        Assertions.assertThat(saved.id).isNotNull()
        Assertions.assertThat(saved.memberId).isEqualTo(notification.memberId)
        Assertions.assertThat(saved.title).isEqualTo(notification.title)
        Assertions.assertThat(saved.message).isEqualTo(notification.message)
        Assertions.assertThat(saved.url).isEqualTo(notification.url)
    }

    @Test
    @DisplayName("findById: Notification 반환한다")
    fun findById() {
        val notification = notificationMongoRepository.save(
            Notification.create(
                memberId = 1L,
                title = "title",
                message = "message",
                url = "url",
            )
        )

        val result = notificationMongoRepository.findById(notification.id!!)

        Assertions.assertThat(result).isPresent()
        Assertions.assertThat(result.get().memberId).isEqualTo(notification.memberId)
        Assertions.assertThat(result.get().title).isEqualTo(notification.title)
        Assertions.assertThat(result.get().message).isEqualTo(notification.message)
        Assertions.assertThat(result.get().url).isEqualTo(notification.url)
    }

    @Test
    @DisplayName("findAll: Notification 목록 반환한다")
    fun findAll() {
        notificationMongoRepository.save(
            Notification.create(
                memberId = 1L,
                title = "title1",
                message = "msg1",
                url = "url1"
            )
        )
        notificationMongoRepository.save(
            Notification.create(
                memberId = 2L,
                title = "title2",
                message = "msg2",
                url = "url2"
            )
        )

        val result = notificationMongoRepository.findAll()

        Assertions.assertThat(result).hasSize(2)
    }

    @Test
    @DisplayName("deleteById: Notification 삭제한다")
    fun deleteById() {
        val notification = notificationMongoRepository.save(
            Notification.create(
                memberId = 1L,
                title = "title",
                message = "message",
                url = "url",
            )
        )
        val id = notification.id!!

        notificationMongoRepository.deleteById(id)

        val deleted = notificationMongoRepository.findById(id)
        Assertions.assertThat(deleted).isEmpty()
    }
}