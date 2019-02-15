package com.hzy.mediaselector

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hzy.selector.MediaSelector
import com.hzy.selector.MediaSelectorActivity
import com.hzy.selector.resolver.Const
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
                MediaSelector.with(this).openMediaSelectorActivity()
//                startActivity(Intent(this, MediaSelectorActivity::class.java))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Const.CODE_RESULT_MEDIA -> {
                if (requestCode == Const.CODE_REQUEST_MEDIA) {
                    val data = MediaSelector.resultMediaFile(data)
                    Toast.makeText(this, data?.size.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
