package plus.a66.bot.core

import love.forte.simbot.FragileSimbotApi
import love.forte.simbot.bot.Bot
import love.forte.simbot.bot.OriginBotManager
import org.ktorm.database.Database
import org.ktorm.entity.forEach
import org.ktorm.entity.sequenceOf
import org.springframework.stereotype.Component
import plus.a66.bot.core.entity.GroupBootStatusE
import java.util.*

/**
 * @author VarleyT
 * @date 2023/1/3
 */
@Component
class BotCore(
    private val database: Database
) {

    init {
        initBootMap()
    }

    private fun initBootMap() {
        BootMap = HashMap()
        database.sequenceOf(GroupBootStatusE).forEach {
            BootMap[it.groupCode] = it.status
        }
    }

    companion object {
        lateinit var BootMap: MutableMap<Long, Boolean>

        val StartTime: Date = Date()

        @OptIn(FragileSimbotApi::class)
        fun getBot(): Bot {
            return OriginBotManager.getAnyBot()
        }
    }
}