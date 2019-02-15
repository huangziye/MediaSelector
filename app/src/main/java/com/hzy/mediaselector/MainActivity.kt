package com.hzy.mediaselector

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hzy.selector.MediaSelector
import com.hzy.selector.resolver.Const
import com.hzy.selector.util.GlideUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_open.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_open -> {
                val options = MediaSelector.MediaOptions()
                options.isShowCamera = true
                options.isShowVideo = true
                options.isCrop = true
                options.maxChooseMedia = 9
                options.themeColor = R.color.colorAccent
                MediaSelector.with(this).setMediaOptions(options).openMediaSelectorActivity()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Const.CODE_RESULT_MEDIA -> {
                if (requestCode == Const.CODE_REQUEST_MEDIA) {
                    val data = MediaSelector.obtainMediaFile(data)
                    GlideUtil.loadImage(this@MainActivity, data!![0].filePath!!, iv)
                }
            }
        }
    }
}
