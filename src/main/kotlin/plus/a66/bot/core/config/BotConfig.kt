package plus.a66.bot.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * @author VarleyT
 * @date 2023/1/3
 */
@Configuration
@ConfigurationProperties(prefix = "bot")
class BotConfig {
    var admin: Long? = null

    var api: API? = null

    class API {
        lateinit var douyin: String
    }
}