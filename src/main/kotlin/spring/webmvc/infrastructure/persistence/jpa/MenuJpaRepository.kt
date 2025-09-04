package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import spring.webmvc.domain.model.entity.Menu

interface MenuJpaRepository : JpaRepository<Menu, Long> {

    @Query(
        """
        select m
        from Menu m
        join m._permissionMenus pm
        join pm.permission p
        where p.name in (:permissions)
    """
    )
    fun findByPermissions(@Param("permissions") permissions: Iterable<String>): List<Menu>
}