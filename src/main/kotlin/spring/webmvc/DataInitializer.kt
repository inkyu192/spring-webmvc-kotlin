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


        // 권한 생성
        val productReader: Permission = permissionRepository.save(Permission.create("PRODUCT_READER"))
        val productWriter: Permission = permissionRepository.save(Permission.create("PRODUCT_WRITER"))
        val orderReader: Permission = permissionRepository.save(Permission.create("ORDER_READER"))
        val orderWriter: Permission = permissionRepository.save(Permission.create("ORDER_WRITER"))
        val permissionReader: Permission = permissionRepository.save(Permission.create("PERMISSION_READER"))
        val permissionWriter: Permission = permissionRepository.save(Permission.create("PERMISSION_WRITER"))
        val menuReader: Permission = permissionRepository.save(Permission.create("MENU_READER"))
        val menuWriter: Permission = permissionRepository.save(Permission.create("MENU_WRITER"))
        val roleReader: Permission = permissionRepository.save(Permission.create("ROLE_READER"))
        val roleWriter: Permission = permissionRepository.save(Permission.create("ROLE_WRITER"))


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


        // 메뉴 생성
        val product = Menu.create("상품 관리", "/products")
        product.addPermission(productReader)

        val order = Menu.create("주문 관리", "/orders")
        order.addPermission(orderReader)

        val settings = Menu.create("설정")
        settings.addPermission(permissionReader)
        settings.addPermission(menuReader)
        settings.addPermission(roleReader)

        val permissionManage = Menu.create("권한 관리", "/permissions")
        permissionManage.addPermission(permissionReader)
        permissionManage.updateParent(settings)

        val menuManage = Menu.create("메뉴 관리", "/menus")
        menuManage.addPermission(menuReader)
        menuManage.updateParent(settings)

        val roleManage = Menu.create("역할 관리", "/roles")
        roleManage.addPermission(roleReader)
        roleManage.updateParent(settings)

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
    }
}