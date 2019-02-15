package com.hzy.selector

import android.app.Activity
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import com.hzy.selector.bean.MediaSelectorFile
import com.hzy.selector.resolver.Const


/**
 *
 * @author: ziye_huang
 * @date: 2019/2/12
 */
class MediaSelector {
    private var mMediaOptions = MediaSelector.defaultOptions
    private var mSoftActivity: Activity? = null
    private var mSoftFragment: Fragment? = null

    private constructor(activity: Activity) {
        mSoftActivity = activity
    }

    private constructor(fragment: Fragment) {
        mSoftFragment = fragment
    }

    fun setMediaOptions(options: MediaOptions): MediaSelector {
        this.mMediaOptions = options
        return this
    }

    fun openMediaSelectorActivity() {
        if (mSoftActivity != null) {
            val intent = Intent(mSoftActivity, MediaActivity::class.java)
            intent.putExtra(Const.KEY_OPEN_MEDIA, mMediaOptions)
            mSoftActivity?.startActivityForResult(intent, Const.CODE_REQUEST_MEDIA)
        } else if (mSoftFragment != null) {
            val intent = Intent(mSoftFragment?.context, MediaActivity::class.java)
            intent.putExtra(Const.KEY_OPEN_MEDIA, mMediaOptions)
            mSoftFragment?.startActivityForResult(intent, Const.CODE_REQUEST_MEDIA)
        }
    }


    class MediaOptions : Parcelable {
        var maxChooseMedia = Const.MAX_CHOOSE_MEDIA
        var isCompress: Boolean = false
        var isShowCamera: Boolean = false
        var isShowVideo: Boolean = false
        @ColorRes
        var themeColor = R.color.colorTheme
        var isCrop: Boolean = false
        var scaleX = 1
        var scaleY = 1
        var cropWidth = 720
        var cropHeight = 720

        constructor()

        protected constructor(parcel: Parcel) {
            maxChooseMedia = parcel.readInt()
            isCompress = parcel.readByte().toInt() != 0
            isShowCamera = parcel.readByte().toInt() != 0
            isShowVideo = parcel.readByte().toInt() != 0
            themeColor = parcel.readInt()
            isCrop = parcel.readByte().toInt() != 0
            scaleX = parcel.readInt()
            scaleY = parcel.readInt()
            cropWidth = parcel.readInt()
            cropHeight = parcel.readInt()
        }

        override fun describeContents(): Int = 0

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(maxChooseMedia)
            dest.writeByte((if (isCompress) 1 else 0).toByte())
            dest.writeByte((if (isShowCamera) 1 else 0).toByte())
            dest.writeByte((if (isShowVideo) 1 else 0).toByte())
            dest.writeInt(themeColor)
            dest.writeByte((if (isCrop) 1 else 0).toByte())
            dest.writeInt(scaleX)
            dest.writeInt(scaleY)
            dest.writeInt(cropWidth)
            dest.writeInt(cropHeight)
        }

        companion object CREATOR : Creator<MediaOptions> {
            override fun createFromParcel(parcel: Parcel): MediaOptions {
                return MediaOptions(parcel)
            }

            override fun newArray(size: Int): Array<MediaOptions?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {

        fun with(activity: Activity): MediaSelector = MediaSelector(activity)

        fun with(fragment: Fragment): MediaSelector = MediaSelector(fragment)

        fun resultMediaFile(data: Intent?): List<MediaSelectorFile>? =
            data?.getParcelableArrayListExtra(Const.KEY_REQUEST_MEDIA_DATA)

        val defaultOptions: MediaOptions
            @Synchronized get() = MediaOptions()
    }
}