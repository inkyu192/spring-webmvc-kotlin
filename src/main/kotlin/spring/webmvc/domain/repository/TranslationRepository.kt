package spring.webmvc.domain.repository

import spring.webmvc.domain.model.entity.Translation

interface TranslationRepository {
    fun findAll(): List<Translation>
}
