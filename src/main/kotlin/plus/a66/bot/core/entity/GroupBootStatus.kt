package plus.a66.bot.core.entity

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.boolean
import org.ktorm.schema.int
import org.ktorm.schema.long

/**
 * @author VarleyT
 * @date 2023/1/2
 */
interface GroupBootStatus : Entity<GroupBootStatus> {
    val id: Int

    // 群号
    val groupCode: Long

    // 状态
    val status: Boolean
}

object GroupBootStatusE : Table<GroupBootStatus>("group_status") {
    val id = int("id")
        .primaryKey()
        .bindTo { it.id }
    val groupCode = long("group_code")
        .bindTo { it.groupCode }
    val status = boolean("status")
        .bindTo { it.status }
}

val Database.groupBootStatus get() = this.sequenceOf(GroupBootStatusE)
