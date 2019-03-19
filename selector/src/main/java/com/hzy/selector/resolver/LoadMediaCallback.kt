package com.hzy.selector.resolver

import com.hzy.selector.bean.MediaSelectorFolder

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/12
 */
interface LoadMediaCallback {
    fun onLoadMediaFinish(data: ArrayList<MediaSelectorFolder>)
}