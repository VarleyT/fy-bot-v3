package plus.a66.bot.core.util

import cn.hutool.core.img.ImgUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.crypto.digest.MD5
import cn.hutool.http.HttpUtil
import plus.a66.bot.core.BotCore
import java.io.File
import javax.imageio.ImageIO

/**
 * @author VarleyT
 * @date 2023/1/20
 */
object FileUtil {

    fun String.saveAsTempFile(fileType: FileType): File {
        val response = HttpUtil.createGet(this)
            .setFollowRedirects(false)
            .execute()
        val bytes = response.bodyBytes()
        val stream = response.bodyStream()
        val file = FileUtil.file(getTempPath(MD5().digestHex16(this) + fileType.VALUE))
        if (fileType == FileType.IMAGE) {
            ImgUtil.write(ImageIO.read(stream), file)
        } else {
            file.writeBytes(bytes)
        }
        return file
    }

    private fun getTempPath(fileName: String): String = BotCore.TEMP_PATH + fileName
}

@Suppress("unused")
enum class FileType(val VALUE: String) {
    IMAGE(".jpg"),
    MUSIC(".mp3"),
    VIDEO(".mp4"),
    OTHER(".tmp")
}