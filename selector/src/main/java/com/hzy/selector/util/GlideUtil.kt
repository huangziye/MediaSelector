package com.hzy.selector.util

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.hzy.selector.R

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/12
 */
object GlideUtil {

    fun loadImage(context: Context, url: String, imageView: ImageView) {
        val options = RequestOptions().centerCrop().placeholder(R.mipmap.ic_image_background)
            .error(R.mipmap.ic_image_background)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(context).asBitmap().apply(options).load(url).into(imageView)
    }

    fun loadImage(context: Context, url: String, imageView: ImageView, isCenterCrop: Boolean) {
        var options = RequestOptions().placeholder(R.mipmap.ic_image_background)
            .error(R.mipmap.ic_image_background).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        options = if (isCenterCrop) {
            options.centerCrop()
        } else {
            options.centerInside()
        }
        Glide.with(context).asBitmap().apply(options).load(url).into(imageView)
    }

    fun loadImage(context: Context, @DrawableRes resId: Int, imageView: ImageView) {
        val options = RequestOptions().centerInside().placeholder(R.mipmap.ic_image_background)
            .error(R.mipmap.ic_image_background).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        Glide.with(context).asBitmap().apply(options).load(resId).into(imageView)
    }
}