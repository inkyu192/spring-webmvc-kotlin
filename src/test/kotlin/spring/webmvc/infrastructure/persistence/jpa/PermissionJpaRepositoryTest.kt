package spring.webmvc.infrastructure.persistence.jpa

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.infrastructure.config.DataJpaTestConfig

@DataJpaTest
@Import(DataJpaTestConfig::class)
class PermissionJpaRepositoryTest {

    @Autowired
    private lateinit var permissionJpaRepository: PermissionJpaRepository

    @Test
    @DisplayName("save: Permission 저장 후 반환한다")
    fun save() {
        val permission = Permission.create("name")

        val saved = permissionJpaRepository.save(permission)

        Assertions.assertThat(saved.id).isNotNull()
        Assertions.assertThat(saved.name).isEqualTo(permission.name)
    }

    @Test
    @DisplayName("findById: Permission 반환한다")
    fun findById() {
        val permission = permissionJpaRepository.save(Permission.create("name"))

        val result = permissionJpaRepository.findById(permission.id!!)

        Assertions.assertThat(result).isPresent()
        Assertions.assertThat(result.get().name).isEqualTo(permission.name)
    }

    @Test
    @DisplayName("findAll: Permission 목록 반환한다")
    fun findAll() {
        permissionJpaRepository.save(Permission.create("name1"))
        permissionJpaRepository.save(Permission.create("name2"))

        val result = permissionJpaRepository.findAll()

        Assertions.assertThat(result).hasSize(2)
    }

    @Test
    @DisplayName("deleteById: Permission 삭제한다")
    fun deleteById() {
        val permission = permissionJpaRepository.save(Permission.create("name"))
        val id = permission.id!!

        permissionJpaRepository.deleteById(id)

        val deleted = permissionJpaRepository.findById(id)
        Assertions.assertThat(deleted).isEmpty()
    }
}