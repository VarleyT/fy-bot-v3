package plus.a66.bot

import love.forte.simboot.spring.autoconfigure.EnableSimbot
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import xyz.cssxsh.mirai.tool.FixProtocolVersion

/**
 * @author VarleyT
 * @date 2022/12/31
 */

@EnableSimbot
@SpringBootApplication
class BotApplication

fun main(args: Array<String>) {
    FixProtocolVersion.update()
    runApplication<BotApplication>(*args)
}