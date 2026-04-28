package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Curation
import spring.webmvc.domain.model.enums.CurationPlacement

interface CurationRepository {
    fun findById(id: Long): Curation?
    fun findAllByPlacement(placement: CurationPlacement): List<Curation>
    fun save(curation: Curation): Curation
}
