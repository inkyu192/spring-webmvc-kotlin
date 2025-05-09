package spring.webmvc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class SpringWebmvcKotlinApplication

fun main(args: Array<String>) {
    runApplication<SpringWebmvcKotlinApplication>(*args)
}
