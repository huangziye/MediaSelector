package com.hzy.selector.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.hzy.selector.resolver.MediaScanner
import java.io.File


/**
 *
 * @author: ziye_huang
 * @date: 2019/2/11
 */
object FileUtil {

    /**
     * 获取父文件夹名字
     */
    fun getParentFileName(filePath: String): String = getParentFile(filePath).name

    /**
     * 获取父文件夹绝对路径
     */
    fun getParentFilePath(filePath: String): String = getParentFile(filePath).absolutePath

    /**
     * 获取File对象
     */
    fun getParentFile(filePath: String): File {
        val file = File(filePath)
        if (file.exists() && file.isFile) {
            return file.parentFile
        }
        throw NullPointerException("file must exists or isFile!")
    }

    fun existsFile(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists() && file.isFile
    }

    fun outCameraFileDirectory(context: Context): File {
        var rootFile = context.filesDir
        rootFile = File("${rootFile.absolutePath}/images")
        if (!rootFile.exists() || !rootFile.isDirectory) {
            rootFile.mkdirs()
        }
        return rootFile
    }


    fun outFileDirectory(context: Context, folderName: String): File {
        var rootFile = context.filesDir
        rootFile = File(rootFile.absolutePath + "/" + folderName)
        if (!rootFile.exists() || !rootFile.isDirectory) {
            rootFile.mkdirs()
        }
        return rootFile
    }

    /**
     * 生成图片文件
     */
    fun resultImageFile(context: Context): File {
        return File(outCameraFileDirectory(context).absolutePath, "temp" + System.currentTimeMillis() + ".jpg")
    }

    /**
     * 生成图片文件
     */
    fun resultImageFile(context: Context, folderName: String): File {
        return File(outFileDirectory(context, folderName).absolutePath, "temp" + System.currentTimeMillis() + ".jpg")
    }

    fun fileToUri(context: Context, file: File, intent: Intent): Uri {
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = context.packageName + ".provider"
            uri = FileProvider.getUriForFile(context, authority, file)
            val resolveInfoData =
                context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveInfoData != null && resolveInfoData.size > 0)
                for (resolveInfo in resolveInfoData) {
                    val packageName = resolveInfo.activityInfo.packageName
                    context.grantUriPermission(
                        packageName,
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
        } else {
            uri = Uri.fromFile(file)
        }
        return uri
    }

    fun scanImage(context: Context, file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val ms = MediaScanner(context, file)
            ms.refresh()
        } else {
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = Uri.fromFile(file)
            context.sendBroadcast(intent)
        }
    }

    fun getFileWidth(path: String): Int = getBitmapOptions(path).outWidth

    fun getFileHeight(path: String): Int = getBitmapOptions(path).outHeight

    private fun getBitmapOptions(path: String): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        options.inScaled = false
        options.inMutable = true
        val bitmap = BitmapFactory.decodeFile(path, options)
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
        return options
    }
}