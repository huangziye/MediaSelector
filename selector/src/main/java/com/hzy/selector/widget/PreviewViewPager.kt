package com.hzy.selector.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/13
 */
class PreviewViewPager(ctx: Context, attrs: AttributeSet? = null) :
    ViewPager(ctx, attrs) {
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.dispatchTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        try {
            return super.onTouchEvent(ev)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}