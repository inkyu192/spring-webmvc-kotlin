package spring.webmvc.infrastructure.persistence.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import spring.webmvc.domain.model.entity.Menu

interface MenuJpaRepository : JpaRepository<Menu, Long> {

    @Query(
        """
		select m
		from Menu m
		left join m._permissionMenus mp
		left join mp.permission p
		where p.name in (:permissions)
        and m.parent is null
	"""
    )
    fun findRootMenus(permissions: Iterable<String>): List<Menu>

    @Query(
        """
		select m
		from Menu m
		left join m._permissionMenus mp
		left join mp.permission p
		where p.name in (:permissions)
        and m.parent.id = :parentId
	"""
    )
    fun findChildMenus(permissions: Iterable<String>, parentId: Long): List<Menu>
}