package com.hzy.selector.util

import android.os.Build
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.view.*


/**
 *
 * @author: ziye_huang
 * @date: 2019/2/12
 */
object ScreenUtil {
    fun screenWidth(context: Context): Int = context.resources.displayMetrics.widthPixels

    fun screenHeight(context: Context): Int = context.resources.displayMetrics.heightPixels

    fun dp2px(context: Context, dpValue: Float): Int =
        (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()

    fun px2dp(context: Context, pxValue: Float): Int =
        (pxValue / context.resources.displayMetrics.density + 0.5f).toInt()

    fun setDefaultRootViewSize(context: Context, rootView: ViewGroup) {
        val rootParams = rootView.layoutParams
        rootParams.width = -1
        rootParams.height = dp2px(context, 45.0f)
        rootView.layoutParams = rootParams
    }

    fun getStatusBarHeight(context: Context): Int = context.resources
        .getDimensionPixelSize(context.resources.getIdentifier("status_bar_height", "dimen", "android"))

    /**
     * 判断导航栏是否显示
     *
     * @param context 上下文
     * @return 导航栏是否显示
     */
    fun isShowDeviceHasNavigationBar(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            val size = Point()
            val realSize = Point()
            display.getSize(size)
            display.getRealSize(realSize)
            return realSize.y !== size.y
        } else {
            val menu = ViewConfiguration.get(context).hasPermanentMenuKey()
            val back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
            return !menu && !back
        }
    }

    /**
     * 隐藏导航栏
     *
     * @param activity 显示界面
     */
    fun hideTransparentNavigation(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decorView = activity.window.decorView
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            activity.window.navigationBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 显示导航栏
     *
     * @param activity 显示界面
     */
    fun showTransparentNavigation(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val decorView = activity.window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    /**
     * 判断导航栏高度
     *
     * @param context 上下文
     * @return 导航栏高度
     */
    fun getNavigationHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }
}