package plus.a66.bot.core.util

import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.Member
import love.forte.simbot.message.Image.Key.toImage
import love.forte.simbot.resources.Resource.Companion.toResource
import love.forte.simbot.tryToLong
import plus.a66.bot.core.util.FileUtil.saveAsTempFile
import java.util.*

/**
 * @author VarleyT
 * @date 2023/1/3
 */
object SimbootUtil {
    val Group.code: Long get() = this.id.tryToLong()
    val Friend.code: Long get() = this.id.tryToLong()
    val Member.code: Long get() = this.id.tryToLong()
    fun String.toImage() = this.saveAsTempFile(FileType.IMAGE).toResource().toImage()
    operator fun Date.minus(date: Date): String {
        val timeStamp = (date.time - this.time) / 1000
        val seconds = timeStamp % 60
        val minutes = timeStamp / 60 % 60
        val hours = timeStamp / (60 * 60) % 24
        val days = timeStamp / (60 * 60 * 24)
        return "${
            if (days == 0L) ""
            else "${days}日"
        }${
            if (hours == 0L) ""
            else "${hours}时"
        }${
            if (minutes == 0L) ""
            else "${minutes}分"
        }${seconds}秒"
    }
}