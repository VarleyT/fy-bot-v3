package plus.a66.bot.entertainment.constant

/**
 * @author VarleyT
 * @date 2023/2/25
 */
enum class GenshinPrayType(val VALUE: String) {
    /**
     * 角色池
     */
    ROLE("/RolePray"),

    /**
     * 武器池
     */
    ARM("/ArmPray"),

    /**
     * 常驻池
     */
    PERM("/PermPray"),

    /**
     * 全角色池
     */
    FULL_ROLE("/FullRolePray"),

    /**
     * 全武器池
     */
    FULL_ARM("/FullArmPray"),


    /**
     * 单抽
     */
    PRAY_ONE("/PrayOne"),

    /**
     * 十连
     */
    PRAY_TEN("/PrayTen")
}