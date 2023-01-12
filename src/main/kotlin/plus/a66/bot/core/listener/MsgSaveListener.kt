package plus.a66.bot.core.listener

import love.forte.simboot.annotation.Listener
import love.forte.simbot.Api4J
import love.forte.simbot.event.GroupMessageEvent
import org.ktorm.database.Database
import org.ktorm.dsl.insertAndGenerateKey
import org.springframework.stereotype.Component
import plus.a66.bot.core.entity.GroupMessageTbl
import plus.a66.bot.core.util.SimbootUtil.code
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author VarleyT
 * @date 2023/1/12
 */
@Component
@Suppress("unused")
class MsgSaveListener(
    private var database: Database
) {

    @Listener
    @OptIn(Api4J::class)
    suspend fun GroupMessageEvent.save() {
        database.insertAndGenerateKey(GroupMessageTbl) {
            set(it.groupCode, group.code)
            set(it.groupName, group.name)
            set(it.senderCode, author.code)
            set(it.senderName, author.username)
            set(it.sendTime, with(SimpleDateFormat("yyyy-MM-dd HH:mm:ss")) {
                format(Date(timestamp.millisecond))
            })
            set(it.msgContent, messageContent.messages.toString())
        }
    }
}