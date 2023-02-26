package plus.a66.bot.entertainment.listener

import cn.hutool.cache.CacheUtil
import cn.hutool.cache.impl.FIFOCache
import cn.hutool.core.util.ReUtil
import cn.hutool.http.HttpStatus
import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONObject
import kotlinx.coroutines.launch
import love.forte.simboot.annotation.Filter
import love.forte.simboot.annotation.FilterValue
import love.forte.simboot.filter.MatchType
import love.forte.simbot.Api4J
import love.forte.simbot.component.mirai.message.buildMiraiForwardMessage
import love.forte.simbot.event.GroupMessageEvent
import love.forte.simbot.message.*
import org.springframework.stereotype.Component
import plus.a66.bot.core.annotation.BotListener
import plus.a66.bot.core.config.BotConfig
import plus.a66.bot.core.util.SimbootUtil.code
import plus.a66.bot.core.util.SimbootUtil.toImage
import plus.a66.bot.core.util.sendAsync
import plus.a66.bot.core.util.sendWithRecall
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

    @Filter(value = "\\d\\.\\d{2} [a-zA-Z]{3}:/.*", matchType = MatchType.REGEX_MATCHES)
    @BotListener(time = 10, timeUnit = TimeUnit.SECONDS)
    suspend fun GroupMessageEvent.douyin1() {
        val url = ReUtil.get("https://v\\.douyin\\.com/\\w{7}/", messageContent.plainText, 0)
        if (!url.isNullOrEmpty()) {
            bot.launch {
                sendWithRecall("检测到抖音视频链接，正在进行智能解析~")
            }
            douyin(url)
        }
    }

    @Filter(value = "抖音解析{{videoUrl}}", matchType = MatchType.REGEX_MATCHES)
    @BotListener(time = 10, timeUnit = TimeUnit.SECONDS)
    suspend fun GroupMessageEvent.douyin(@FilterValue("videoUrl") videoUrl: String) {
        val url = ReUtil.get("https://v\\.douyin\\.com/\\w{7}/", videoUrl, 0)
        if (url.isNullOrEmpty()) {
            bot.launch {
                sendWithRecall("没有找到待解析的视频链接，抖音解析+[分享链接]即可解析~")
            }
            return
        }
        val response = HttpUtil.createPost(botConfig.api!!.douyin)
            .body(JSON.toJSONString(mapOf("url" to url)))
            .execute()
        when (response.status) {
            HttpStatus.HTTP_OK -> {
                try {
                    val data = JSON.parseObject(response.body()).getJSONObject("data")
                    val desc = data.getString("desc")
                    val authorName = data.getJSONObject("author").getString("nickname")
                    val authorDesc = data.getJSONObject("author").getString("signature")
                    val authorCover = data.getJSONObject("author").getJSONObject("avatar").getJSONArray("url_list")
                        ?.takeIf { !it.isEmpty() }?.run { getString(0) }

                    val musicObj: JSONObject? = data.getJSONObject("music")
                    val musicTitle = musicObj?.getString("title")
                    val musicAuthor = musicObj?.getString("author")
                    val musicCoverUrl = musicObj?.getJSONObject("cover")?.getJSONArray("url_list")
                        ?.takeIf { !it.isEmpty() }?.run { getString(0) }
                    val musicPlayUrl = musicObj?.getJSONObject("play_addr")?.getJSONArray("url_list")
                        ?.takeIf { !it.isEmpty() }?.run { getString(0) }

                    val videoMsg: Messages? = data.getJSONObject("video")?.run {
                        val videoPlayAddr = String.format(
                            "https://aweme.snssdk.com/aweme/v1/play/?video_id=%s&ratio=1080p&line=0",
                            getJSONObject("play_addr").getString("uri")
                        )
                        val videoCoverAddr = getJSONObject("cover").getJSONArray("url_list")
                            ?.takeIf { !it.isEmpty() }?.run { getString(0) }
                        messages(
                            videoCoverAddr?.toImage() ?: "".toText(),
                            desc.toText() + "\n",
                            videoPlayAddr.toText()
                        )
                    }
                    val imgList = ArrayList<String>()
                    data.getJSONObject("image")?.apply {
                        with(getJSONArray("images")) {
                            if (size > 6) {
                                bot.launch { sendWithRecall("待解析的资源过多，请耐心等待~") }
                            }
                            for (i in 0 until size) {
                                imgList.add(getJSONObject(i).getJSONArray("url_list").getString(0))
                            }
                        }
                    }

                    sendWithRecall(300, buildMiraiForwardMessage {
                        bot.says("【抖音解析】")
                        bot.says("视频无水印解析成功~")
                        bot.says(
                            messages(
                                authorCover?.toImage() ?: "".toText(),
                                "@$authorName\n".toText(),
                                authorDesc.toText()
                            )
                        )
                        if (musicObj != null) {
                            bot.says(
                                messages(
                                    musicCoverUrl?.toImage() ?: "".toText(),
                                    "$musicTitle-$musicAuthor\n".toText(),
                                    musicPlayUrl?.toText() ?: "暂无播放链接".toText()
                                )
                            )
                        }
                        if (videoMsg != null) {
                            bot.says(videoMsg)
                        }
                        if (imgList.isNotEmpty()) {
                            bot.says(messages(desc.toText()))
                            // 分组容量
                            val groupSize = 15
                            if (imgList.size < groupSize) {
                                for (i in 0 until imgList.size) {
                                    bot.says(messages(imgList[i].toImage(), imgList[i].toText()))
                                }
                            } else {
                                // 分组发送
                                for (i in 0 until groupSize) {
                                    bot.says(messages(imgList[i].toImage(), imgList[i].toText()))
                                }

                                for (i in groupSize until imgList.size step groupSize) {
                                    bot.launch {
                                        sendWithRecall(300,
                                            buildMiraiForwardMessage {
                                                if (imgList.size - i < groupSize) {
                                                    for (j in i until imgList.size) {
                                                        bot.says(messages(imgList[j].toImage(), imgList[j].toText()))
                                                    }
                                                } else {
                                                    for (j in i until i + groupSize) {
                                                        bot.says(messages(imgList[j].toImage(), imgList[j].toText()))
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    })
                } catch (e: NullPointerException) {
                    sendWithRecall("视频解析失败，请稍后重试")
                    return
                }
            }

            HttpStatus.HTTP_BAD_REQUEST -> sendWithRecall(JSON.parseObject(response.body()).getString("msg"))
            else -> sendWithRecall("服务端错误，请检查api是否失效！")
        }
    }

    var cache: FIFOCache<Long, Pair<Messages, Boolean>> = CacheUtil.newFIFOCache(30)

    @OptIn(Api4J::class)
    @BotListener(time = 1, timeUnit = TimeUnit.SECONDS)
    suspend fun GroupMessageEvent.repeat() {
        if (cache.containsKey(group.code)) {
            val value = cache.get(group.code)
            if (value.first == messageContent.messages) {
                if (!value.second) {
                    cache.put(group.code, messageContent.messages to true)
                    sendAsync(messageContent.messages)
                }
                return
            }
        }
        cache.put(group.code, messageContent.messages to false)
    }
}