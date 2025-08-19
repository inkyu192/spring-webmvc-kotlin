package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Curation

interface CurationRepository {
    fun findById(id: Long): Curation?
    fun findExposed(): List<Curation>
    fun save(curation: Curation): Curation
}