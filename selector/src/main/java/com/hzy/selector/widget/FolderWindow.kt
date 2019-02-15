package com.hzy.selector.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.PopupWindow
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hzy.selector.R
import com.hzy.selector.adapter.MediaFolderAdapter
import com.hzy.selector.bean.MediaSelectorFolder
import com.hzy.selector.listener.OnRecyclerItemClickListener
import com.hzy.selector.resolver.Const
import com.hzy.selector.util.ScreenUtil

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/13
 */
class FolderWindow(private val context: Context, private val folderData: MutableList<MediaSelectorFolder>) {

    private var mPopupWindow: PopupWindow? = null
    private var mFolderAdapter: MediaFolderAdapter? = null
    private var mViewRoot: View? = null
    private var mShowView: View? = null

    init {
        createWindows()
        initEvent()
    }

    private fun initEvent() {
        mViewRoot!!.setOnClickListener { this@FolderWindow.dismissWindows() }
        mFolderAdapter!!.setOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                mOnPopupItemClickListener?.onItemClick(view, position)
                this@FolderWindow.dismissWindows()
            }
        })
    }

    fun getFolderWindow(): PopupWindow {
        return mPopupWindow!!
    }

    fun dismissWindows() {
        windowAnimation(false)
    }

    private fun createWindows() {
        if (mPopupWindow == null) {
            mPopupWindow = PopupWindow(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            mPopupWindow!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.color80000000)))
            mPopupWindow!!.isClippingEnabled = false
            //mPopupWindow.setOutsideTouchable(true);
            @SuppressLint("InflateParams")
            val inflateView = LayoutInflater.from(context).inflate(R.layout.popup_folder_window, null, false)
            val mRvFolder = inflateView.findViewById<RecyclerView>(R.id.rv_folder)
            mViewRoot = inflateView.findViewById(R.id.ll_root)
            mRvFolder.layoutManager = LinearLayoutManager(context)
            mFolderAdapter = MediaFolderAdapter(context, folderData)
            mRvFolder.itemAnimator = DefaultItemAnimator()
            mRvFolder.adapter = mFolderAdapter
            mPopupWindow!!.contentView = inflateView
        }
    }

    fun showWindow(@NonNull view: View) {
        this.mShowView = view
        mPopupWindow!!.showAtLocation(
            view,
            Gravity.BOTTOM,
            0,
            ScreenUtil.dp2px(view.context, Const.DEFAULT_VIEW_HEIGHT)
        )
        windowAnimation(true)
    }

    fun setOnPopupItemClickListener(onPopupItemClickListener: OnPopupItemClickListener) {
        this.mOnPopupItemClickListener = onPopupItemClickListener
    }

    private var mOnPopupItemClickListener: OnPopupItemClickListener? = null

    interface OnPopupItemClickListener {
        fun onItemClick(@NonNull view: View, position: Int)
    }

    @SuppressLint("ObjectAnimatorBinding")
    private fun windowAnimation(isOpen: Boolean) {
        val objectAnimator: ObjectAnimator = if (isOpen) {
            ObjectAnimator.ofFloat(
                mViewRoot,
                "translationY",
                (ScreenUtil.screenHeight(context) - ScreenUtil.dp2px(context, mShowView!!.height.toFloat())).toFloat(),
                0f
            )
        } else {
            ObjectAnimator.ofFloat(
                mViewRoot, "translationY", 0f,
                (ScreenUtil.screenHeight(context) - ScreenUtil.dp2px(context, mShowView!!.height.toFloat())).toFloat()
            )
        }
        objectAnimator.interpolator = LinearInterpolator()
        objectAnimator.duration = 300
        objectAnimator.start()
        objectAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                if (!isOpen) {
                    mPopupWindow!!.dismiss()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
                if (!isOpen) {
                    mPopupWindow!!.dismiss()
                }
            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
    }
}