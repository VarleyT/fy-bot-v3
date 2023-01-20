package plus.a66.bot.entertainment.listener

import cn.hutool.core.util.ReUtil
import cn.hutool.http.HttpStatus
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simboot.filter.MatchType
import love.forte.simbot.component.mirai.message.buildMiraiForwardMessage
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.message.*
import org.springframework.stereotype.Component
import plus.a66.bot.core.annotation.BotListener
import plus.a66.bot.core.common.send
import plus.a66.bot.core.config.BotConfig
import plus.a66.bot.core.util.SimbootUtil.toImage
import java.util.concurrent.TimeUnit

/**
 * @author VarleyT
 * @date 2023/1/16
 */
@Suppress("unused")
@Component
class GroupListener(
    private val botConfig: BotConfig
) {

    @Filter("抖音解析{{videoUrl}}", matchType = MatchType.REGEX_MATCHES)
    @BotListener(time = 10, timeUnit = TimeUnit.SECONDS)
    suspend fun GroupMessageEvent.douyin(@FilterValue("videoUrl") videoUrl: String) {
        val url = ReUtil.get("https://v\\.douyin\\.com/\\w{7}/", videoUrl, 0)
        if (url.isNullOrEmpty()) {
            send("没有找到待解析的视频链接，抖音解析+[分享链接]即可解析~")
            return
        }
        val response = HttpUtil.createPost(botConfig.api!!.douyin)
            .body(JSON.toJSONString(mapOf("url" to url)))
            .execute()
        when (response.status) {
            HttpStatus.HTTP_OK -> {
                val data = JSON.parseObject(response.body()).getJSONObject("data")
                val desc = data.getString("desc")
                val authorName = data.getJSONObject("author")?.getString("nickname")
                val authorDesc = data.getJSONObject("author")?.getString("signature")
                val authorCover =
                    data.getJSONObject("author")?.getJSONObject("avatar_medium")
                        ?.getJSONArray("url_list")?.getString(0)
                lateinit var messages: Messages
                data.getJSONObject("video")?.apply {
                    val videoPlayAddr = String.format(
                        "https://aweme.snssdk.com/aweme/v1/play/?video_id=%s&ratio=1080p&line=0",
                        getJSONObject("play_addr").getString("uri")
                    )
                    val videoCoverAddr = getJSONObject("cover").getJSONArray("url_list").getString(0)
                    messages = messages(
                        videoCoverAddr.toImage(),
                        desc.toText() + "\n",
                        videoPlayAddr.toText()
                    )
                }
                data.getJSONObject("image")?.apply {
                    messages = messages("".toText())
                    with(getJSONArray("images")) {
                        for (i in 0 until size) {
                            val imageUrl = getJSONObject(i).getJSONArray("url_list").getString(0)
                            messages += messages(imageUrl.toImage(), imageUrl.toText())
                        }
                    }
                    messages += messages("\n$desc".toText())
                }
                send(buildMiraiForwardMessage {
                    bot.says("【抖音解析】")
                    bot.says("视频无水印解析成功~")
                    bot.says(
                        messages(
                            authorCover?.toImage()
                                ?: "暂无头像信息\n".toText(),
                            "@${authorName ?: "null"}\n".toText(),
                            authorDesc?.toText()
                                ?: "暂未获取到信息\n".toText()
                        )
                    )
                    bot.says(messages)
                })
            }

            HttpStatus.HTTP_BAD_REQUEST -> send(JSON.parseObject(response.body()).getString("msg"))
            else -> send("服务端错误，请检查api是否失效！")
        }
    }
}