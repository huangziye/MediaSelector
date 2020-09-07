package com.hzy.selector.bean

import android.os.Parcelable
import android.os.Parcel


/**
 *
 * @author: ziye_huang
 * @date: 2019/2/12
 */
class MediaSelectorFolder() : Parcelable {
    var folderName: String? = null
    var folderPath: String? = null
    var fileData: ArrayList<MediaSelectorFile> = arrayListOf()
    var isCheck: Boolean = false
    lateinit var firstFilePath: String
    var isAllVideo: Boolean = false

    constructor(parcel: Parcel) : this() {
        folderName = parcel.readString()
        folderPath = parcel.readString()
        fileData = parcel.createTypedArrayList(MediaSelectorFile) ?: arrayListOf()
        isCheck = parcel.readByte().toInt() != 0
        firstFilePath = parcel.readString()!!
        isAllVideo = parcel.readByte().toInt() != 0
    }

    companion object CREATOR : Parcelable.Creator<MediaSelectorFolder> {
        override fun createFromParcel(parcel: Parcel): MediaSelectorFolder {
            return MediaSelectorFolder(parcel)
        }

        override fun newArray(size: Int): Array<MediaSelectorFolder?> {
            return arrayOfNulls(size)
        }
    }

    /**
     * 判断文件夹的路径是否一致判断是否相等
     *
     * @param obj
     * @return
     */
    override fun equals(obj: Any?): Boolean {
        if (obj == null || folderPath == null)
            return false
        if (obj is MediaSelectorFolder) {
            val folder = obj as MediaSelectorFolder?
            return this.folderPath == folder!!.folderPath
        }
        return super.equals(obj)
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(folderName)
        dest.writeString(folderPath)
        dest.writeTypedList(fileData)
        dest.writeByte((if (isCheck) 1 else 0).toByte())
        dest.writeString(firstFilePath)
        dest.writeByte((if (isAllVideo) 1 else 0).toByte())
    }
}