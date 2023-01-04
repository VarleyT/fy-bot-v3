package plus.a66.bot.core.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * @author VarleyT
 * @date 2023/1/3
 */
@Component
@ConfigurationProperties(prefix = "bot")
class BotConfig {
    var admin: Long? = null
}