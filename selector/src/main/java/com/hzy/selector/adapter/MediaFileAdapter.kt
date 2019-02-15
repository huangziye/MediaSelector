package com.hzy.selector.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
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
class MediaFileAdapter : RecyclerView.Adapter<MediaFileAdapter.ViewHolder> {
    private val mContext: Context
    private val mData: MutableList<MediaSelectorFile>
    private val mOptions: MediaSelector.MediaOptions
    private var mOnCheckedMediaListener: OnCheckedMediaListener? = null
    private var mOnRecyclerItemClickListener: OnRecyclerItemClickListener? = null

    constructor(
        context: Context,
        data: MutableList<MediaSelectorFile>,
        options: MediaSelector.MediaOptions
    ) : super() {
        mContext = context
        mData = data
        mOptions = options
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_media_file_view, parent, false))
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val layoutParams = holder.mIvData.layoutParams
        if (mData[position].isShowCamera) {
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            GlideUtil.loadImage(mContext, R.mipmap.ic_camera, holder.mIvData)
            holder.mIvCheck.visibility = View.GONE
            holder.mViewLay.visibility = View.GONE
            holder.mRlVideo.visibility = View.GONE
        } else {
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            holder.mIvCheck.visibility = if (mOptions.maxChooseMedia > 1) View.VISIBLE else View.GONE
            GlideUtil.loadImage(mContext, mData[position].filePath, holder.mIvData)
            holder.mIvCheck.setImageResource(if (mData[position].isCheck) R.mipmap.ic_image_checked else R.mipmap.ic_image_unchecked)
            holder.mViewLay.visibility = if (mData[position].isCheck) View.VISIBLE else View.GONE

            if (mData[position].isVideo) {
                holder.mRlVideo.visibility = View.VISIBLE
                holder.mTvDuration.text = DateUtil.videoDuration(mData[position].videoDuration)
            } else {
                holder.mRlVideo.visibility = View.GONE
            }


        }
        holder.mIvData.layoutParams = layoutParams

        holder.mIvCheck.setOnClickListener {
            mOnCheckedMediaListener?.onChecked(mData[position].isCheck, position)
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