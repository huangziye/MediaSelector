package com.hzy.selector.util

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/12
 */
object DateUtil {
    fun videoDuration(videoDuration: Long): String {
        val sb = StringBuilder()
        if (videoDuration >= 1000) {
            val second = (videoDuration / 1000).toInt()
            if (second / 60 >= 1) {
                val minute = second / 60
                val remainderSecond = second % 60
                sb.append(minute).append(if (remainderSecond >= 10) ":$remainderSecond" else ":0$remainderSecond")
            } else {
                if (second >= 10) {
                    sb.append("0:").append(second)
                } else {
                    sb.append("0:0").append(second)
                }
            }
        } else {
            sb.append("0:01")
        }
        return sb.toString()
    }
}