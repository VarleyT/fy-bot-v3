package plus.a66.bot.core.config

import org.ktorm.database.Database
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * @author VarleyT
 * @date 2023/1/3
 */
@Configuration
class KtormConfiguration {
    @Autowired
    lateinit var dataSource: DataSource

    @Bean
    fun database(): Database {
        return Database.connectWithSpringSupport(dataSource)
    }
}