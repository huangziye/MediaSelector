package com.hzy.selector.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.hzy.selector.R
import com.hzy.selector.bean.MediaSelectorFile
import com.hzy.selector.listener.OnRecyclerItemClickListener
import com.hzy.selector.util.GlideUtil

/**
 * 选择目录适配器
 * @author: ziye_huang
 * @date: 2019/2/12
 */
class MediaCheckAdapter(private val context: Context, private val mediaCheckDataList: MutableList<MediaSelectorFile>) :
    RecyclerView.Adapter<MediaCheckAdapter.ViewHolder>() {

    private var mPreviewMedia: MediaSelectorFile? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_check_media_view, parent, false))

    override fun getItemCount(): Int = if (mediaCheckDataList.isEmpty()) 0 else mediaCheckDataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mIvMediaType.visibility = if (mediaCheckDataList[position].isVideo) View.VISIBLE else View.GONE
        GlideUtil.loadImage(context, mediaCheckDataList[position].filePath!!, holder.mIvItem)
        holder.mIvItem.setBackgroundResource(if (mPreviewMedia!!.filePath == mediaCheckDataList[position].filePath) R.drawable.shape_media_check else R.drawable.shape_media_uncheck)
        holder.itemView.setOnClickListener { v ->
            mOnRecyclerItemClickListener?.onItemClick(v, position)
        }
    }

    /**
     * 更新数据源
     */
    fun notifyCheckData(previewMedia: MediaSelectorFile) {
        mPreviewMedia = previewMedia
        notifyDataSetChanged()
    }

    /**
     * 移除数据
     */
    fun removeItemNotifyData(position: Int) {
        mediaCheckDataList.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * 添加一条数据
     */
    fun addItemNotifyData(previewMedia: MediaSelectorFile) {
        mediaCheckDataList.add(previewMedia)
        notifyItemInserted(mediaCheckDataList.size - 1)
    }

    private var mOnRecyclerItemClickListener: OnRecyclerItemClickListener? = null

    fun setOnRecyclerItemClickListener(onRecyclerItemClickListener: OnRecyclerItemClickListener) {
        this.mOnRecyclerItemClickListener = onRecyclerItemClickListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIvItem: ImageView = itemView.findViewById(R.id.iv_item)
        val mIvMediaType: ImageView = itemView.findViewById(R.id.iv_media_type)
    }
}