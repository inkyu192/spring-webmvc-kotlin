package spring.webmvc.infrastructure.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import javax.sql.DataSource

@Testcontainers
class MySQLTestContainerConfig {

    companion object {
        @Container
        private val mysql = MySQLContainer("mysql:8.4.7").apply { start() }
    }

    @Bean
    fun dataSource(): DataSource {
        val config = HikariConfig().apply {
            jdbcUrl = mysql.jdbcUrl
            username = mysql.username
            password = mysql.password
            driverClassName = "com.mysql.cj.jdbc.Driver"
        }
        return HikariDataSource(config)
    }
}