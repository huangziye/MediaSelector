package com.hzy.selector.eventbus

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/14
 */
internal class MessageEvent(private val eventType: Int, private val data: Any?) {

    companion object {
        /**
         * 在预览页面处理数据并返回
         */
        val HANDING_DATA_IN_PREVIEW_PAGE = 1

        /**
         * 在预览页面选择或取消文件选择
         */
        val SELECTOR_IN_PREVIEW_PAGE = 2
    }


    fun getEventType(): Int {
        return eventType
    }

    fun getData(): Any? {
        return data
    }

}