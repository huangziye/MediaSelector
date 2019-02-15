package com.hzy.selector.resolver

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import java.io.File

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/11
 */
class MediaScanner(context: Context, private val imageFile: File) :
    MediaScannerConnection.MediaScannerConnectionClient {

    private val mMediaScannerConnection: MediaScannerConnection = MediaScannerConnection(context, this)

    override fun onMediaScannerConnected() {
        mMediaScannerConnection.scanFile(imageFile.absolutePath, "image")
    }

    override fun onScanCompleted(path: String?, uri: Uri?) {
        mMediaScannerConnection.disconnect()
    }

    fun refresh() {
        if (!mMediaScannerConnection.isConnected) {
            mMediaScannerConnection.connect()
        }
    }
}