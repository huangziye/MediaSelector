package com.hzy.selector.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hzy.selector.MediaSelector
import com.hzy.selector.R
import com.hzy.selector.bean.MediaSelectorFile
import com.hzy.selector.listener.OnRecyclerItemClickListener
import com.hzy.selector.util.DateUtil
import com.hzy.selector.util.GlideUtil
import com.hzy.selector.util.ScreenUtil


/**
 *
 * @author: ziye_huang
 * @date: 2019/2/12
 */
class MediaFileAdapter(
    private val context: Context, private var mediaFileDataList: MutableList<MediaSelectorFile>,
    private var options: MediaSelector.MediaOptions
) : RecyclerView.Adapter<MediaFileAdapter.ViewHolder>() {

    private var mOnCheckedMediaListener: OnCheckedMediaListener? = null
    private var mOnRecyclerItemClickListener: OnRecyclerItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_media_file_view, parent, false))
    }

    override fun getItemCount(): Int = mediaFileDataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutParams = holder.mIvData.layoutParams
        if (mediaFileDataList[position].isShowCamera) {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            GlideUtil.loadImage(context, R.mipmap.ic_camera, holder.mIvData)
            holder.mIvCheck.visibility = View.GONE
            holder.mViewLay.visibility = View.GONE
            holder.mRlVideo.visibility = View.GONE
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            holder.mIvCheck.visibility = if (options.maxChooseMedia > 1) View.VISIBLE else View.GONE
            GlideUtil.loadImage(context, mediaFileDataList[position].filePath!!, holder.mIvData)
            holder.mIvCheck.setImageResource(if (mediaFileDataList[position].isCheck) R.mipmap.ic_image_checked else R.mipmap.ic_image_unchecked)
            holder.mViewLay.visibility = if (mediaFileDataList[position].isCheck) View.VISIBLE else View.GONE

            if (mediaFileDataList[position].isVideo) {
                holder.mRlVideo.visibility = View.VISIBLE
                holder.mTvDuration.text = DateUtil.videoDuration(mediaFileDataList[position].videoDuration)
            } else {
                holder.mRlVideo.visibility = View.GONE
            }
        }
        holder.mIvData.layoutParams = layoutParams

        holder.mIvCheck.setOnClickListener {
            mOnCheckedMediaListener?.onChecked(mediaFileDataList[position].isCheck, position)
        }
        holder.itemView.setOnClickListener { v ->
            mOnRecyclerItemClickListener?.onItemClick(v, position)
        }
    }

    fun setOnCheckedMediaListener(onCheckedMediaListener: OnCheckedMediaListener) {
        mOnCheckedMediaListener = onCheckedMediaListener
    }

    fun setOnRecyclerItemClickListener(onRecyclerItemClickListener: OnRecyclerItemClickListener) {
        mOnRecyclerItemClickListener = onRecyclerItemClickListener
    }

    interface OnCheckedMediaListener {
        fun onChecked(isCheck: Boolean, position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mRootGroup = itemView.findViewById<ViewGroup>(R.id.rl_root)
        val mIvData = itemView.findViewById<ImageView>(R.id.iv_data)
        val mIvCheck = itemView.findViewById<ImageView>(R.id.iv_check)
        val mViewLay = itemView.findViewById<View>(R.id.view_lay)
        val mRlVideo = itemView.findViewById<RelativeLayout>(R.id.rl_video)
        val mTvDuration = itemView.findViewById<TextView>(R.id.tv_duration)

        init {
            setRootGroupParams(mRootGroup)
        }

        /**
         * 设置padding
         */
        private fun setRootGroupParams(viewGroup: ViewGroup) {
            val mGroupParams = viewGroup.layoutParams
            mGroupParams.width = viewGroup.context.resources.displayMetrics.widthPixels / 4
            mGroupParams.height = viewGroup.context.resources.displayMetrics.widthPixels / 4
            viewGroup.layoutParams = mGroupParams
            viewGroup.setPadding(
                ScreenUtil.dp2px(viewGroup.context, 1.5f), ScreenUtil.dp2px(viewGroup.context, 1.5f),
                ScreenUtil.dp2px(viewGroup.context, 1.5f), ScreenUtil.dp2px(viewGroup.context, 1.5f)
            )
        }
    }
}