package plus.a66.bot.core.util

import cn.hutool.http.HttpRequest
import cn.hutool.http.HttpResponse

/**
 * @author VarleyT
 * @date 2023/3/1
 */
object BotHttpUtil {
    fun get(url: String): HttpRequest = HttpRequest.get(url)

    inline fun get(url: String, block: HttpRequest.() -> HttpRequest = { this }): HttpResponse =
        get(url).block().execute()

    fun post(url: String): HttpRequest = HttpRequest.post(url)

    inline fun post(url: String, block: HttpRequest.() -> HttpRequest = { this }): HttpResponse =
        post(url).block().execute()

    fun HttpRequest.param(key: Any, value: Any): HttpRequest {
        val list = params()?.toMutableList()
        if (list == null) {
            this.url += "?$key=$value"
        } else {
            list.add(key.toString() to value.toString())
            val url = this.url.split("?")[0]
            this.url = url + list.joinToString("&", "?") { "${it.first}=${it.second}" }
        }
        return this
    }

    private fun HttpRequest.params(): List<Pair<String, String>>? {
        val var1 = this.url.split("?")
        return if (var1.size > 1) {
            val var2 = var1[1].split("&")
            var2.map {
                val var3 = it.split("=")
                var3[0] to var3[1]
            }.toList()
        } else {
            null
        }
    }
}