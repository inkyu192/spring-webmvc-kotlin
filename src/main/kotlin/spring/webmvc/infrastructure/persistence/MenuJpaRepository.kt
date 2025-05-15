package spring.webmvc.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import spring.webmvc.domain.model.entity.Menu

interface MenuJpaRepository : JpaRepository<Menu, Long> {

    @Query(
        """
		select m
		from Menu m
		left join m._menuPermissions mp
		left join mp.permission p
		where p.name in (:permissions)
	"""
    )
    fun findAllByPermissionNameIn(permissions: Iterable<String>): List<Menu>
}