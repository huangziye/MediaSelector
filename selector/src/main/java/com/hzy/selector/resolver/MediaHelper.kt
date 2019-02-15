package com.hzy.selector.resolver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.Nullable
import androidx.annotation.RequiresPermission
import com.hzy.selector.bean.MediaSelectorFile
import com.hzy.selector.bean.MediaSelectorFolder
import com.hzy.selector.util.FileUtil


/**
 *
 * @author: ziye_huang
 * @date: 2019/2/11
 */
class MediaHelper(private val activity: Activity) {
    private val QUERY_URI = MediaStore.Files.getContentUri("external")
    //查询的内容
    @SuppressLint("InlinedApi")
    private val IMAGE_PROJECTION = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.WIDTH,
        MediaStore.Files.FileColumns.HEIGHT,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.DISPLAY_NAME
    )
    @SuppressLint("InlinedApi")
    private val ALL_PROJECTION = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.WIDTH,
        MediaStore.Files.FileColumns.HEIGHT,
        MediaStore.Files.FileColumns.SIZE,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION
    )
    private val IMAGE_SELECTION_TYPE =
        MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " AND " + MediaStore.MediaColumns.SIZE + ">0"
    private val ALL_SELECTION_TYPE =
        "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=? OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)" +
                " AND " + MediaStore.MediaColumns.SIZE + ">0"
    private val IMAGE_WHERE_TYPE = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
    private val ALL_WHERE_TYPE = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )
    private val SORT_ORDER = MediaStore.Files.FileColumns.DATE_MODIFIED + " desc"

//    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA])
    fun loadMedia(isShowCamera: Boolean, isShowVideo: Boolean, @Nullable onResult: LoadMediaCallback?) {
        val cursor = activity.contentResolver.query(
            QUERY_URI,
            if (isShowVideo) ALL_PROJECTION else IMAGE_PROJECTION,
            if (isShowVideo) ALL_SELECTION_TYPE else IMAGE_SELECTION_TYPE,
            if (isShowVideo) ALL_WHERE_TYPE else IMAGE_WHERE_TYPE,
            SORT_ORDER
        )
        if (cursor != null && !cursor.isClosed && cursor.count > 0) {
            //所有的图片
            val mAllFileData = mutableListOf<MediaSelectorFile>()

            //所有文件夹
            val folderData = ArrayList<MediaSelectorFolder>()
            val mVideoFileData = mutableListOf<MediaSelectorFile>()

            while (cursor.moveToNext()) {
                val mediaFile = MediaSelectorFile()
                mediaFile.fileName =
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME))

                mediaFile.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA))
                if (TextUtils.isEmpty(mediaFile.fileName) || TextUtils.isEmpty(mediaFile.filePath)
                    || TextUtils.getTrimmedLength(mediaFile.fileName) == 0 || TextUtils.getTrimmedLength(mediaFile.filePath) == 0 || mediaFile.fileName!!.endsWith(
                        ".gif"
                    )
                ) {
                    continue
                }
                mediaFile.fileSize = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mediaFile.width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH))
                    mediaFile.height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT))
                }
                if (FileUtil.existsFile(mediaFile.filePath!!)) {
                    mediaFile.folderName = FileUtil.getParentFileName(mediaFile.filePath!!)
                    mediaFile.folderPath = FileUtil.getParentFilePath(mediaFile.filePath!!)
                } else {
                    continue
                }
                mediaFile.isVideo =
                    cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)) === MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                if (mediaFile.isVideo) {
                    mediaFile.videoDuration =
                        cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                    if (mediaFile.videoDuration >= 60 * 60 * 1000 || mediaFile.videoDuration < 1000) {
                        continue
                    }
                    mVideoFileData.add(mediaFile)
                }

                val mediaFolder = MediaSelectorFolder()
                mediaFolder.folderPath = mediaFile.folderPath
                //首先判断该文件的父文件夹有没有在集合中？有的话直接把文件加入对应的文件夹：没有就新建一个文件夹再添加进去
                if (folderData.size > 0 && folderData.contains(mediaFolder) && folderData.indexOf(mediaFolder) >= 0) {
                    folderData[folderData.indexOf(mediaFolder)].fileData.add(mediaFile)
                } else {
                    mediaFolder.folderName = mediaFile.folderName
                    mediaFolder.fileData.add(mediaFile)
                    mediaFolder.firstFilePath = mediaFile.filePath!!
                    folderData.add(mediaFolder)
                }
                mAllFileData.add(mediaFile)

            }
            cursor.close()
            if (mAllFileData.size > 0) {
                if (isShowCamera) {
                    val cameraMediaFile = MediaSelectorFile()
                    cameraMediaFile.isShowCamera = true
                    mAllFileData.add(0, cameraMediaFile)
                }

                val allMediaFolder = MediaSelectorFolder()
                allMediaFolder.folderPath = Const.ALL_FILE
                allMediaFolder.folderName = Const.ALL_FILE
                allMediaFolder.firstFilePath =
                    if (isShowCamera) mAllFileData[1].filePath!! else mAllFileData[0].filePath!!
                allMediaFolder.fileData.addAll(mAllFileData)
                allMediaFolder.isCheck = true
                folderData.add(0, allMediaFolder)
                //增加视频目录
                if (mVideoFileData.size > 0) {
                    val videoMediaFolder = MediaSelectorFolder()
                    videoMediaFolder.folderPath = Const.ALL_VIDEO
                    videoMediaFolder.folderName = Const.ALL_VIDEO
                    videoMediaFolder.firstFilePath = mVideoFileData[0].filePath!!
                    videoMediaFolder.fileData.addAll(mVideoFileData)
                    videoMediaFolder.isAllVideo = true
                    folderData.add(folderData.indexOf(allMediaFolder) + 1, videoMediaFolder)
                }
                if (onResult != null && folderData.size > 0) {
                    onResult.onLoadMediaFinish(folderData)
                }
            }

        } else {
            Toast.makeText(activity, "没有文件", Toast.LENGTH_SHORT).show()
        }
    }
}