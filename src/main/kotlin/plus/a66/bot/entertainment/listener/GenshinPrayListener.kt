package plus.a66.bot.entertainment.listener

import cn.hutool.http.HttpUtil
import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.to
import love.forte.simboot.annotation.Filter
import love.forte.simboot.filter.MatchType
import love.forte.simbot.Api4J
import love.forte.simbot.component.mirai.message.buildMiraiForwardMessage
import love.forte.simbot.definition.Member
import love.forte.simbot.event.GroupMessageEvent
import org.springframework.stereotype.Component
import plus.a66.bot.core.annotation.BotListener
import plus.a66.bot.core.config.BotConfig
import plus.a66.bot.core.util.SimbootUtil.code
import plus.a66.bot.core.util.SimbootUtil.toImage
import plus.a66.bot.core.util.send
import plus.a66.bot.core.util.sendWithRecall
import plus.a66.bot.entertainment.constant.GenshinPrayType
import plus.a66.bot.entertainment.constant.GenshinPrayType.*
import plus.a66.bot.entertainment.dto.GenshinPrayGood
import plus.a66.bot.entertainment.dto.GenshinPrayInfo

/**
 * @author VarleyT
 * @date 2023/2/25
 */
@Component
@Suppress("unused")
class GenshinPrayListener(
    val botConfig: BotConfig
) {

    @OptIn(Api4J::class)
    @Filter(value = "(角色|武器|常驻|全武器|全角色).*", matchType = MatchType.REGEX_MATCHES)
    @BotListener
    suspend fun GroupMessageEvent.genshin() {
        var text: String? = messageContent.plainText.trim()
        val prayType = when (text!!.substring(0..1)) {
            "角色" -> ROLE
            "武器" -> ARM
            "常驻" -> PERM
            else -> when (text.substring(0..2)) {
                "全武器" -> FULL_ARM
                "全角色" -> FULL_ROLE
                else -> null
            }
        }
        text = when (prayType) {
            ROLE, ARM, PERM -> text.substring(2)
            FULL_ARM, FULL_ROLE -> text.substring(3)
            else -> null
        }
        val isTen = when (text?.substring(0..1)) {
            "单抽" -> false
            "十连" -> true
            else -> null
        }
        if (prayType == null || isTen == null) {
            send("请输入正确的指令：(角色|武器|常驻|全武器|全角色)单抽/十连")
            return
        }
        val prayInfo = pray(author, prayType, isTen)
        if (prayInfo == null) {
            send("请求出错！请检查api是否失效")
            return
        }

        val surplus = when (prayType) {
            ROLE -> prayInfo.role90Surplus
            ARM -> prayInfo.arm80Surplus
            PERM -> prayInfo.perm90Surplus
            FULL_ROLE -> prayInfo.fullRole90Surplus
            FULL_ARM -> prayInfo.fullArm80Surplus
            else -> 0
        }
        sendWithRecall(240, buildMiraiForwardMessage {
            bot.says(prayInfo.imgHttpUrl.toImage())
            if (prayInfo.star5Goods.isNotEmpty()) {
                bot.says(
                    """
                    恭喜你获得了5星[${
                        prayInfo.star5Goods.map { it.to<GenshinPrayGood>() }.joinToString("&") { it.goodsName }
                    }]！真是羡煞旁人！
                    本轮祈愿共计消耗道具*${prayInfo.star5Cost}
                """.trimIndent()
                )
            } else {
                bot.says("很遗憾没有抽中武器，快去洗洗手吧！")
            }
            bot.says(
                """
                当前祈愿池：${prayInfo.star5Up.map { it.to<GenshinPrayGood>() }.joinToString("&") { it.goodsName }}
                本次消耗道具：*${prayInfo.prayCount}
                距离保底剩余${surplus}抽
            """.trimIndent()
            )
        })
    }

    private fun pray(member: Member, prayType: GenshinPrayType, isTen: Boolean): GenshinPrayInfo? {
        val api = botConfig.api?.genshin?.api
        val authorization = botConfig.api?.genshin?.authorization
        val request = HttpUtil.createGet(
            api + prayType.VALUE +
                    if (isTen) {
                        PRAY_TEN.VALUE
                    } else {
                        PRAY_ONE.VALUE
                    }
                    + "?memberCode=${member.code}&memberName=${member.username}"
        )
            .header("authorzation", authorization)
        val response = request.execute()
        if (response.isOk) {
            return JSON.parseObject(response.body()).to<GenshinPrayInfo>("data")
        }
        return null
    }
}