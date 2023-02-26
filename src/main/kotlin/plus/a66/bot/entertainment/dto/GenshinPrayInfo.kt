package plus.a66.bot.entertainment.dto

import com.alibaba.fastjson2.JSONObject

/**
 * @author VarleyT
 * @date 2023/2/25
 */
data class GenshinPrayInfo(
    /**
     * 祈愿次数
     */
    val prayCount: Int,
    /**
     * 角色池剩余多少抽大保底
     */
    val role180Surplus: Int,
    /**
     * 角色池剩余多少抽保底
     */
    val role90Surplus: Int,
    /**
     * 武器池剩余多少抽保底
     */
    val arm80Surplus: Int,
    /**
     * 武器池当前命定值
     */
    val armAssignValue: Int,
    /**
     * 常驻池剩余多少抽保底
     */
    val perm90Surplus: Int,
    /**
     * 全角色池剩余多少抽保底
     */
    val fullRole90Surplus: Int,
    /**
     * 全武器池剩余多少抽保底
     */
    val fullArm80Surplus: Int,
    /**
     * 当前蛋池剩余多少抽十连保底
     */
    val surplus10: Int,
    /**
     * 本次获取五星物品累计消耗多少抽，如果本次未抽出五星时，值为0
     */
    val star5Cost: Int,
    /**
     * 本日Api剩余可调用次数
     */
    val apiDailyCallSurplus: Int,
    /**
     * 图片在tomcat中的http地址
     */
    val imgHttpUrl: String,
    /**
     * 图片大小(byte)
     */
    val imgSize: Long,
    /**
     * 相对于图片生成目录的地址
     */
    val imgPath: String,
    /**
     * 图片的base64字符串
     */
    val imgBase64: String?,
    /**
     * 本次祈愿获取的3星物品列表
     */
    val star3Goods: List<JSONObject>,
    /**
     * 本次祈愿获取的4星物品列表
     */
    val star4Goods: List<JSONObject>,
    /**
     * 本次祈愿获取的5星物品列表
     */
    val star5Goods: List<JSONObject>,
    /**
     * 当前蛋池中的4星UP列表
     */
    val star4Up: List<JSONObject>,
    /**
     * 当前蛋池中的5星UP列表
     */
    val star5Up: List<JSONObject>
)
