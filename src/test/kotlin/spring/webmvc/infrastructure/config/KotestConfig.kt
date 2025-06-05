package spring.webmvc.infrastructure.config

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.extensions.spring.SpringTestExtension

class KotestConfig : AbstractProjectConfig() {
    override fun extensions() = listOf(SpringTestExtension())
}