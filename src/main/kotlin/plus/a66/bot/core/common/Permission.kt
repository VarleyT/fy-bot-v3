package plus.a66.bot.core.common

import love.forte.simbot.definition.Role

/**
 * @author VarleyT
 * @date 2023/1/2
 */
enum class Permission(private val LEVEL: Int) {
    /**
     * 机器人管理员
     */
    BOT_OWNER(4),

    /**
     * 群主
     */
    OWNER(3),

    /**
     * 群管理员
     */
    ADMINISTRATOR(2),

    /**
     * 群成员
     */
    MEMBER(1);

    operator fun compareTo(role: Role): Int {
        val i = this.LEVEL - Permission.valueOf(role.name).LEVEL
        return if (i > 0) 1
        else if (i < 0) -1
        else 0
    }
}