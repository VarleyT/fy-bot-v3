package plus.a66.bot.core.annotation

import love.forte.simboot.annotation.Listener
import plus.a66.bot.core.common.Permission
import java.util.concurrent.TimeUnit

/**
 * @author VarleyT
 * @date 2023/1/2
 */

@Listener
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BotListener(

    /**
     * 间隔次数
     *
     * 默认：3
     */
    val count: Int = 3,

    /**
     * 间隔时长
     *
     * 等于0：关闭
     *
     * 默认：2000
     */
    val time: Long = 2000,

    /**
     * 间隔单位
     *
     * 默认：ms
     */
    val timeUnit: TimeUnit = TimeUnit.MILLISECONDS,

    /**
     * 是否需要启动
     *
     * 默认：true
     */
    val isBoot: Boolean = true,

    /**
     * 需要的权限
     *
     * 默认：Permission.MEMBER
     */
    val permission: Permission = Permission.MEMBER
)
