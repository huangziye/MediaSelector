package com.hzy.selector.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hzy.selector.R
import com.hzy.selector.bean.MediaSelectorFolder
import com.hzy.selector.listener.OnRecyclerItemClickListener
import com.hzy.selector.util.GlideUtil

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/13
 */
class MediaFolderAdapter(private val context: Context, private val mediaFolderData: MutableList<MediaSelectorFolder>) :
    RecyclerView.Adapter<MediaFolderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_media_folder, parent, false))
    }

    override fun getItemCount(): Int = if (mediaFolderData.isEmpty()) 0 else mediaFolderData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        GlideUtil.loadImage(context, mediaFolderData[position].firstFilePath, holder.mIvLeft)

        holder.mTvCount.text = context.getString(
            R.string.how_match_pieces,
            mediaFolderData[position].fileData.size.toString()
        )
        holder.mTvTitle.text = mediaFolderData[position].folderName
        holder.mIvCheck.visibility = if(mediaFolderData[position].isCheck) View.VISIBLE else View.GONE
        holder.mIvVideoStype.visibility = if (mediaFolderData[position].isAllVideo) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener { v ->
            selectFolderData(mediaFolderData, position)
            mOnRecyclerItemClickListener?.onItemClick(v, position)
        }
    }

    /**
     * 选择某个目录
     */
    private fun selectFolderData(data: List<MediaSelectorFolder>, position: Int) {
        if (data.size > position) {
            if (!data[position].isCheck) {
                for (i in data.indices) {
                    if (i == position) {
                        mediaFolderData[position].isCheck = true
                    } else if (mediaFolderData[i].isCheck) {
                        mediaFolderData[i].isCheck = false
                    }
                }
                notifyDataSetChanged()
            }
        }
    }

    private var mOnRecyclerItemClickListener: OnRecyclerItemClickListener? = null

    fun setOnRecyclerItemClickListener(onRecyclerItemClickListener: OnRecyclerItemClickListener) {
        mOnRecyclerItemClickListener = onRecyclerItemClickListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvLeft = itemView.findViewById<ImageView>(R.id.iv_left)
        val mTvTitle = itemView.findViewById<TextView>(R.id.tv_title)
        val mTvCount = itemView.findViewById<TextView>(R.id.tv_count)
        val mIvCheck = itemView.findViewById<ImageView>(R.id.iv_check)
        val mIvVideoStype = itemView.findViewById<ImageView>(R.id.iv_video_type)
    }
}