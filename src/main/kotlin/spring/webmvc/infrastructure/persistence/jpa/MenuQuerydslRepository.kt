package spring.webmvc.infrastructure.persistence.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import spring.webmvc.domain.model.entity.Menu
import spring.webmvc.domain.model.entity.QPermission.permission
import spring.webmvc.domain.model.entity.QPermissionMenu.permissionMenu

@Repository
class MenuQuerydslRepository(
    private val jpaQueryFactory: JPAQueryFactory,
) {
    fun findByPermissions(permissions: Iterable<String>): List<Menu> {
        return jpaQueryFactory
            .select(permissionMenu.menu)
            .from(permissionMenu)
            .join(permissionMenu.permission, permission)
            .where(permission.name.`in`(permissions.toList()))
            .fetch()
    }
}