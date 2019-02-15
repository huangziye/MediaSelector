package com.hzy.selector.bean

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.NonNull
import android.text.TextUtils
import com.hzy.selector.util.FileUtil
import utils.bean.ImageConfig
import java.io.File
import java.lang.NullPointerException


/**
 *
 * @author: ziye_huang
 * @date: 2019/2/11
 */
class MediaSelectorFile() : Parcelable {
    lateinit var fileName: String
    lateinit var filePath: String
    lateinit var folderName: String
    lateinit var folderPath: String
    var fileSize: Int = 0
    var width: Int = 0
    var height: Int = 0
    var isCheck: Boolean = false
    var isShowCamera: Boolean = false
    var isVideo: Boolean = false
    var videoDuration: Long = 0


    companion object CREATOR : Parcelable.Creator<MediaSelectorFile> {
        override fun createFromParcel(parcel: Parcel): MediaSelectorFile {
            return MediaSelectorFile(parcel)
        }

        override fun newArray(size: Int): Array<MediaSelectorFile?> {
            return arrayOfNulls(size)
        }

        fun checkFileToThis(file: File): MediaSelectorFile {
            val mediaFile = MediaSelectorFile()
            mediaFile.fileName = file.name
            mediaFile.filePath = file.absolutePath
            mediaFile.fileSize = file.length().toInt()
            mediaFile.width = FileUtil.getFileWidth(file.absolutePath)
            mediaFile.height = FileUtil.getFileHeight(file.absolutePath)
            mediaFile.folderName = FileUtil.getParentFileName(file.absolutePath)
            mediaFile.folderPath = FileUtil.getParentFilePath(file.absolutePath)
            mediaFile.isCheck = true
            return mediaFile
        }

        fun thisToDefaultImageConfig(mediaFile: MediaSelectorFile): ImageConfig {
            return ImageConfig.getDefaultConfig(mediaFile.filePath)
        }
    }

    constructor(parcel: Parcel) : this() {
        fileName = parcel.readString()
        filePath = parcel.readString()
        fileSize = parcel.readInt()
        width = parcel.readInt()
        height = parcel.readInt()
        folderName = parcel.readString()
        folderPath = parcel.readString()
        isCheck = parcel.readByte() != 0.toByte()
        isShowCamera = parcel.readByte() != 0.toByte()
        isVideo = parcel.readByte() != 0.toByte()
        videoDuration = parcel.readLong()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(fileName)
        dest.writeString(filePath)
        dest.writeInt(fileSize)
        dest.writeInt(width)
        dest.writeInt(height)
        dest.writeString(folderName)
        dest.writeString(folderPath)
        dest.writeByte((if (isCheck) 1 else 0).toByte())
        dest.writeByte((if (isShowCamera) 1 else 0).toByte())
        dest.writeByte((if (isVideo) 1 else 0).toByte())
        dest.writeLong(videoDuration)
    }

    override fun equals(obj: Any?): Boolean {
        if (obj != null && obj is MediaSelectorFile) {
            val mediaSelectorFile = obj as MediaSelectorFile?
            if (mediaSelectorFile!!.filePath == filePath) return true
        }
        return super.equals(obj)
    }

    fun hasData(): Boolean {
        return (!TextUtils.isEmpty(fileName) && TextUtils.getTrimmedLength(fileName) > 0
                && !TextUtils.isEmpty(filePath) && TextUtils.getTrimmedLength(filePath) > 0
                && fileSize > 0 && width > 0 && height > 0
                && !TextUtils.isEmpty(folderName) && TextUtils.getTrimmedLength(folderName) > 0
                && !TextUtils.isEmpty(folderPath) && TextUtils.getTrimmedLength(folderPath) > 0)
    }

}