package plus.a66.bot.core.entity

import org.ktorm.entity.Entity
import org.ktorm.schema.*

/**
 * @author VarleyT
 * @date 2023/1/12
 */
interface GroupMessage : Entity<GroupMessage> {
    /**
     * 序号
     */
    val id: Int

    /**
     * 群号
     */
    val groupCode: Long

    /**
     * 群名
     */
    val groupName: String

    /**
     * 发送者QQ号
     */
    val senderCode: Long

    /**
     * 发送人昵称
     */
    val senderName: String

    /**
     * 发送时间
     */
    val sendTime: String

    /**
     * 消息正文
     */
    val msgContent: String
}

object GroupMessageTbl : Table<GroupMessage>("group_message") {
    val id = int("id")
        .primaryKey()
        .bindTo { it.id }
    val groupCode = long("group_code")
        .bindTo { it.groupCode }
    val groupName = varchar("group_name")
        .bindTo { it.groupName }
    val senderCode = long("sender_code")
        .bindTo { it.senderCode }
    val senderName = varchar("sender_name")
        .bindTo { it.senderName }
    val sendTime = varchar("send_time")
        .bindTo { it.sendTime }
    val msgContent = text("msg_content")
        .bindTo { it.msgContent }
}
