package plus.a66.bot.core.listener

import love.forte.simboot.annotation.Filter
import love.forte.simbot.Api4J
import love.forte.simbot.Bonus
import love.forte.simbot.definition.Group
import love.forte.simbot.event.FriendMessageEvent
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.event.MessageEvent
import love.forte.simbot.utils.item.toList
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.dsl.update
import org.ktorm.entity.find
import org.springframework.stereotype.Component
import plus.a66.bot.core.BotCore
import plus.a66.bot.core.annotation.BotListener
import plus.a66.bot.core.common.Permission
import plus.a66.bot.core.common.Sender.Companion.send
import plus.a66.bot.core.entity.GroupBootStatusE
import plus.a66.bot.core.entity.groupBootStatus
import plus.a66.bot.core.util.SimbotUtil.code
import plus.a66.bot.core.util.SimbotUtil.minus
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author VarleyT
 * @date 2023/1/2
 */
@Component
@OptIn(Api4J::class)
@Suppress("unused")
class BootListener(
    private var database: Database
) {

    @Filter("开机")
    @BotListener(
        time = 20,
        timeUnit = TimeUnit.SECONDS,
        permission = Permission.ADMINISTRATOR,
        isBoot = false
    )
    suspend fun boot(event: MessageEvent) {
        if (event is GroupMessageEvent) {
            val group = event.group
            val groupCode = group.code
            val msg = changeBootStatus(groupCode, true) ?: send(
                event,
                """
                    【状态】
                    当前群：${group.name}(${group.code})
                    BOT状态：已启动
                    操作者：${event.author.username}(${event.author.code})
                """.trimIndent()
            ).also { return }
            send(event, msg!!)
        } else if (event is FriendMessageEvent) {
            val groupList = BotCore.getBot().groups.toList()
            val msgMap = changeBootStatus(groupList, true)
            with(StringBuilder()) {
                append("【全局状态】\n")
                msgMap.forEach { (k, v) ->
                    append("—${k.name}(${k.code})\n")
                    append("——${v ?: "已启动"}\n")
                }
                send(event, toString())
            }
        }
    }

    @Filter("关机")
    @BotListener(
        time = 20,
        timeUnit = TimeUnit.SECONDS,
        permission = Permission.ADMINISTRATOR,
        isBoot = false
    )
    suspend fun down(event: MessageEvent) {
        if (event is GroupMessageEvent) {
            val group = event.group
            val groupCode = group.code
            val msg = changeBootStatus(groupCode, false) ?: send(
                event,
                """
                    【状态】
                    当前群：${group.name}(${group.code})
                    BOT状态：已关闭
                    操作者：${event.author.username}(${event.author.code})
                """.trimIndent()
            ).also { return }
            send(event, msg!!)
        } else if (event is FriendMessageEvent) {
            val groupList = BotCore.getBot().groups.toList()
            val msgMap = changeBootStatus(groupList, false)
            with(StringBuilder()) {
                append("【全局状态】\n")
                msgMap.forEach { (k, v) ->
                    append("—${k.name}(${k.code})\n")
                    append("——${v ?: "已关闭"}\n")
                }
                send(event, toString())
            }
        }
    }

    @OptIn(Bonus::class)
    @Filter("状态")
    @BotListener(
        time = 1,
        timeUnit = TimeUnit.MINUTES,
        count = 2,
        permission = Permission.ADMINISTRATOR,
        isBoot = false
    )
    suspend fun status(event: MessageEvent) {
        if (event is GroupMessageEvent) {
            val group = event.group
            val groupCode = group.code
            val flag = getBootStatus(groupCode)
            with(StringBuilder()) {
                append("【状态】\n")
                append("当前群：${group.name}(${group.code})\n")
                append("BOT状态：${if (flag) "已启动" else "已关闭"}\n")
                append("运行时间：${BotCore.StartTime - Date()}")
                send(event, toString())
            }
        } else if (event is FriendMessageEvent) {
            with(StringBuilder()) {
                append("【全局状态】\n")
                BotCore.BootMap.forEach { (k, v) ->
                    val group = BotCore.getBot().getGroup(love.forte.simbot.ID.Companion.`$`(k))!!
                    append("—${group.name}(${group.code})\n")
                    append("——${if (v) "已开启" else "已关闭"}\n")
                }
                send(event, toString())
            }
        }
    }

    private fun changeBootStatus(groupList: List<Group>, changedStatus: Boolean): MutableMap<Group, String?> {
        val map: MutableMap<Group, String?> = HashMap()
        for (group in groupList) {
            map[group] = changeBootStatus(group.code, changedStatus)
        }
        return map
    }

    private fun changeBootStatus(groupCode: Long, changedStatus: Boolean): String? {
        val bootStatus = database.groupBootStatus.find { GroupBootStatusE.groupCode eq groupCode }
        if (changedStatus xor (bootStatus ?: return "未在数据库中找到群信息").status) {
            database.update(GroupBootStatusE) {
                set(it.status, changedStatus)
                where {
                    it.groupCode eq groupCode
                }
            }
            BotCore.BootMap[groupCode] = changedStatus
            return null
        }
        return if (changedStatus) "BOT已处于开启状态！" else "BOT已处于关闭状态！"
    }

    private fun getBootStatus(groupCode: Long): Boolean {
        return BotCore.BootMap[groupCode]!!
    }
}