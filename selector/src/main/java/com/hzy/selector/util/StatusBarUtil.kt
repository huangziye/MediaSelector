package com.hzy.selector.util

import android.app.Activity
import android.os.Build
import android.view.WindowManager
import androidx.annotation.ColorInt

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/13
 */
object StatusBarUtil {
    /**
     * 设置状态栏沉浸颜色
     * @param statusBarColor 状态栏的颜色
     * @param useThemestatusBarColor   是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不设置则为透明色
     * @param withoutUseStatusBarColor 是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
     */
    fun setStatusBarColor(
        activity: Activity,
        @ColorInt statusBarColor: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.statusBarColor = statusBarColor
        }
    }
}