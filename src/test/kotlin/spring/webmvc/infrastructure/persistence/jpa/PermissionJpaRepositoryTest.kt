package spring.webmvc.infrastructure.persistence.jpa

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.infrastructure.config.DataJpaTestConfig

@DataJpaTest
@Import(DataJpaTestConfig::class)
class PermissionJpaRepositoryTest(
    private val permissionJpaRepository: PermissionJpaRepository,
) : DescribeSpec({
    describe("save") {
        it("Permission 저장 후 반환한다") {
            val permission = Permission.create("name")

            val saved = permissionJpaRepository.save(permission)

            saved.id shouldNotBe null
            saved.name shouldBe permission.name
        }
    }

    describe("findById") {
        it("Permission 반환한다") {
            val permission = permissionJpaRepository.save(Permission.create("name"))

            val result = permissionJpaRepository.findById(permission.id!!)

            result.isPresent shouldBe true
            result.get().name shouldBe permission.name
        }
    }

    describe("findAll") {
        it("Permission 목록 반환한다") {
            permissionJpaRepository.save(Permission.create("name1"))
            permissionJpaRepository.save(Permission.create("name2"))

            val result = permissionJpaRepository.findAll()

            result shouldHaveSize 2
        }
    }

    describe("deleteById") {
        it("Permission 삭제한다") {
            val permission = permissionJpaRepository.save(Permission.create("name"))
            val id = permission.id!!

            permissionJpaRepository.deleteById(id)

            val deleted = permissionJpaRepository.findById(id)
            deleted.isPresent shouldBe false
        }
    }
})