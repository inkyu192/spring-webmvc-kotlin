package spring.webmvc.presentation.infrastructure.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringExtension

class SpringTestConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringExtension)
}