package plus.a66.bot.entertainment.dto

import java.io.Serializable

/**
 * @author VarleyT
 * @date 2023/2/25
 */
data class GenshinPrayGood(
    /**
     * 物品名称
     */
    val goodsName: String,
    /**
     * 物品类型，武器/角色
     */
    val goodsType: String,
    /**
     * 物品子类型
     */
    val goodsSubType: String,
    /**
     * 稀有类型
     */
    val rareType: String
) : Serializable
