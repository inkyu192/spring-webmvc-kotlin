package spring.webmvc

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.model.entity.Permission
import spring.webmvc.domain.model.entity.Role
import spring.webmvc.domain.repository.MenuRepository
import spring.webmvc.domain.repository.PermissionRepository
import spring.webmvc.domain.repository.RoleRepository

@Component
@Transactional
class DataInitializer(
    private val menuRepository: MenuRepository,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        // 메뉴 생성
        val product = Menu.create("상품 관리", "/products")
        val order = Menu.create("주문 관리", "/orders")
        val settings = Menu.create("설정")
        val permissionManage = Menu.create("권한 관리", "/permissions", settings)
        val menuManage = Menu.create("메뉴 관리", "/menus", settings)
        val roleManage = Menu.create("역할 관리", "/roles", settings)

        menuRepository.saveAll(
            listOf(
                product,
                order,
                settings,
                permissionManage,
                menuManage,
                roleManage
            )
        )

        // 권한 생성
        val productReader = Permission.create("PRODUCT_READER").apply {
            addMenu(product)
        }
        val productWriter = Permission.create("PRODUCT_WRITER")
        val orderReader = Permission.create("ORDER_READER").apply {
            addMenu(order)
        }
        val orderWriter = Permission.create("ORDER_WRITER")
        val permissionReader = Permission.create("PERMISSION_READER").apply {
            addMenu(settings)
            addMenu(permissionManage)
        }
        val permissionWriter = Permission.create("PERMISSION_WRITER")
        val menuReader = Permission.create("MENU_READER").apply {
            addMenu(settings)
            addMenu(menuManage)
        }
        val menuWriter = Permission.create("MENU_WRITER")
        val roleReader = Permission.create("ROLE_READER").apply {
            addMenu(settings)
            addMenu(roleManage)
        }
        val roleWriter = Permission.create("ROLE_WRITER")

        permissionRepository.saveAll(
            listOf(
                productReader,
                productWriter,
                orderReader,
                orderWriter,
                permissionReader,
                permissionWriter,
                menuReader,
                menuWriter,
                roleReader,
                roleWriter
            )
        )

        // 역할 생성
        val roleViewer = Role.create("ROLE_VIEWER")
        roleViewer.addPermission(productReader)
        roleViewer.addPermission(orderReader)
        roleViewer.addPermission(permissionReader)
        roleViewer.addPermission(menuReader)
        roleViewer.addPermission(roleReader)

        val roleProductManager = Role.create("ROLE_PRODUCT_MANAGER")
        roleProductManager.addPermission(productReader)
        roleProductManager.addPermission(productWriter)

        val roleAdmin = Role.create("ROLE_ADMIN")
        roleAdmin.addPermission(productReader)
        roleAdmin.addPermission(productWriter)
        roleAdmin.addPermission(orderReader)
        roleAdmin.addPermission(orderWriter)
        roleAdmin.addPermission(permissionReader)
        roleAdmin.addPermission(permissionWriter)
        roleAdmin.addPermission(menuReader)
        roleAdmin.addPermission(menuWriter)
        roleAdmin.addPermission(roleReader)
        roleAdmin.addPermission(roleWriter)

        roleRepository.saveAll(listOf(roleViewer, roleProductManager, roleAdmin))
    }
}