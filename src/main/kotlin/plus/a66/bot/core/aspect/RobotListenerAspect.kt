package plus.a66.bot.core.aspect

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.event.Event
import love.forte.simbot.event.FriendMessageEvent
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.utils.item.toList
import net.jodah.expiringmap.ExpirationPolicy
import net.jodah.expiringmap.ExpiringMap
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import plus.a66.bot.core.BotCore
import plus.a66.bot.core.Permission
import plus.a66.bot.core.annotation.BotListener
import plus.a66.bot.core.config.BotConfig
import plus.a66.bot.core.util.SimbootUtil.code

/**
 * @author VarleyT
 * @date 2023/1/2
 */
@Aspect
@Component
@OptIn(Api4J::class)
@Suppress("unused")
class RobotListenerAspect(
    private val botConfig: BotConfig
) {

    var expiringMap: ExpiringMap<String, Int> = ExpiringMap.builder()
        .variableExpiration()
        .expirationPolicy(ExpirationPolicy.CREATED)
        .build()

    @Around("@annotation(plus.a66.bot.core.annotation.BotListener) && @annotation(annotation)")
    fun ProceedingJoinPoint.doAround(annotation: BotListener): Any? {
        val event = args.find { it is Event } ?: return proceed()
        if (event is GroupMessageEvent) {
            val group = event.group
            val groupCode = event.group.code
            val author = event.author
            // 时限检查
            if (annotation.interval > 0) {
                with(expiringMap) {
                    val key = "$groupCode: ${signature.name}"
                    when (val i = get(key)) {
                        null -> put(key, 1, annotation.interval, annotation.timeUnit)
                        in 1 until annotation.times -> put(key, i + 1)
                        else -> return null
                    }
                }
            }
            // 开关机状态检查
            if (annotation.isBoot && BotCore.BootMap[groupCode] == false) {
                return null
            }
            // 权限检查
            if (annotation.permission != Permission.MEMBER) {
                val role = runBlocking { author.roles.toList()[0] }
                if (author.code != botConfig.admin && annotation.permission > role) {
                    runBlocking { group.send("权限不足") }
                    return null
                }
            }
        } else if (event is FriendMessageEvent) {
            val code = event.friend.code
            if (code != botConfig.admin) {
                return null
            }
        }
        return proceed()
    }
}