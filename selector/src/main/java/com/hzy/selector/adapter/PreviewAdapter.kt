package com.hzy.selector.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView
import com.hzy.selector.R
import com.hzy.selector.bean.MediaSelectorFile
import com.hzy.selector.util.GlideUtil
import com.hzy.selector.util.ScreenUtil

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/13
 */
class PreviewAdapter(private val mediaFileData: MutableList<MediaSelectorFile>) : PagerAdapter() {

    lateinit var mCbPlay: CheckBox
    private var mChildCount: Int = 0
    private var mOnPreviewViewClickListener: OnPreviewViewClickListener? = null
    private var mOnPreviewVideoClickListener: OnPreviewVideoClickListener? = null

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun getCount(): Int = if (mediaFileData.isEmpty()) 0 else mediaFileData.size

    override fun notifyDataSetChanged() {
        mChildCount = count
        super.notifyDataSetChanged()
    }

    override fun getItemPosition(obj: Any): Int {
        if (mChildCount > 0) {
            mChildCount--
            return PagerAdapter.POSITION_NONE
        }
        return super.getItemPosition(obj)
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        if (mediaFileData[position].isVideo) {
            val inflate =
                LayoutInflater.from(container.context).inflate(R.layout.item_video_play_view, container, false)
            container.addView(inflate)

            val photoView = inflate.findViewById<PhotoView>(R.id.photo_view)
            mCbPlay = inflate.findViewById(R.id.cb_play)
            val layoutParams = photoView.layoutParams
            layoutParams.width = ScreenUtil.screenWidth(container.context)
            layoutParams.height = ScreenUtil.screenHeight(container.context)
            photoView.layoutParams = layoutParams
            GlideUtil.loadImage(container.context, mediaFileData[position].filePath, photoView, false)
            mCbPlay.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked && mOnPreviewVideoClickListener != null) {
                    mOnPreviewVideoClickListener!!.onVideoClick(mCbPlay, position)
                }
            }
            clickPhotoView(photoView)
            return inflate
        } else {
            val photoView = PhotoView(container.context)
            container.addView(photoView)
            val layoutParams = photoView.layoutParams
            layoutParams.width = ScreenUtil.screenWidth(container.context)
            layoutParams.height = ScreenUtil.screenHeight(container.context)
            photoView.layoutParams = layoutParams
            GlideUtil.loadImage(container.context, mediaFileData[position].filePath, photoView, false)
            clickPhotoView(photoView)
            return photoView
        }
    }

    /**
     * 点击PhotoView
     */
    private fun clickPhotoView(@NonNull photoView: PhotoView) {
        photoView.setOnPhotoTapListener { view, x, y ->
            mOnPreviewViewClickListener?.onPreviewClick(view)
        }
    }


    class PreviewPageTransformer : ViewPager.PageTransformer {
        private var viewPager: ViewPager? = null

        override fun transformPage(@NonNull view: View, position: Float) {
            if (viewPager == null) {
                viewPager = view.parent as ViewPager
            }
            val leftInScreen = view.left - viewPager!!.scrollX
            val offsetRate = leftInScreen.toFloat() * 0.08f / viewPager!!.measuredWidth
            val scaleFactor = 1 - Math.abs(offsetRate)
            if (scaleFactor > 0) {
                view.scaleX = scaleFactor
            }
        }
    }

    fun setOnPreviewViewClickListener(onPreviewViewClickListener: OnPreviewViewClickListener) {
        this.mOnPreviewViewClickListener = onPreviewViewClickListener
    }

    fun setOnPreviewVideoClickListener(onPreviewVideoClickListener: OnPreviewVideoClickListener) {
        this.mOnPreviewVideoClickListener = onPreviewVideoClickListener
    }

    interface OnPreviewViewClickListener {
        fun onPreviewClick(view: View)
    }

    interface OnPreviewVideoClickListener {
        fun onVideoClick(view: View, position: Int)
    }

}