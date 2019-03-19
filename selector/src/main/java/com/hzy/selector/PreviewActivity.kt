package com.hzy.selector

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.hzy.compress.ImageCompress
import com.hzy.compress.ImageConfig
import com.hzy.selector.adapter.MediaCheckAdapter
import com.hzy.selector.adapter.PreviewAdapter
import com.hzy.selector.bean.MediaSelectorFile
import com.hzy.selector.eventbus.MessageEvent
import com.hzy.selector.listener.OnRecyclerItemClickListener
import com.hzy.selector.resolver.Const
import com.hzy.selector.util.FileUtil
import com.hzy.selector.util.ScreenUtil
import com.hzy.selector.util.StatusBarUtil
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_preview.*
import org.greenrobot.eventbus.EventBus
import java.io.File

/**
 * 图片预览页面
 * @author: ziye_huang
 * @date: 2019/2/13
 */
class PreviewActivity : AppCompatActivity(), View.OnClickListener {

    private var mCheckMediaData: ArrayList<MediaSelectorFile>? = null
    private var mMediaFileData: ArrayList<MediaSelectorFile>? = null
    private var mPreviewPosition: Int = 0
    private lateinit var mOptions: MediaSelector.MediaOptions
    private lateinit var mPreviewAdapter: PreviewAdapter
    private lateinit var mCheckAdapter: MediaCheckAdapter
    private var mAnimatorSet: AnimatorSet? = null
    private var isShowTitleView = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, R.color.status_bar_color))
        setContentView(R.layout.activity_preview)
        setSupportActionBar(toolbar)

        //设置整个页面的背景为黑色
        window.decorView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
        rv_check_media.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)

        iv_back.setOnClickListener(this)
        tv_selector.setOnClickListener(this)
        tv_finish.setOnClickListener(this)

        initData()
    }

    private fun initData() {
        mCheckMediaData = intent.getParcelableArrayListExtra(Const.KEY_PREVIEW_CHECK_MEDIA)
        if (mCheckMediaData == null) {
            mCheckMediaData = arrayListOf()
        }
        mMediaFileData = intent.getParcelableArrayListExtra(Const.KEY_PREVIEW_DATA_MEDIA)
        mPreviewPosition = intent.getIntExtra(Const.KEY_PREVIEW_POSITION, 0)
        mOptions = intent.getParcelableExtra(Const.KEY_OPEN_MEDIA)
//        mTvTop.mViewRoot.setBackgroundColor(ContextCompat.getColor(this, mOptions.themeColor))
        if (mMediaFileData == null || mMediaFileData!!.size == 0) {
            Toast.makeText(this, "没有预览媒体库文件", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        if (mMediaFileData!![0].isShowCamera && mMediaFileData!![0].filePath == null) {
            mMediaFileData!!.removeAt(0)
            mPreviewPosition--
        }

        if (mCheckMediaData != null && mCheckMediaData!!.size > 0) {
            for (i in mCheckMediaData!!.indices) {
                if (!mMediaFileData!!.contains(mCheckMediaData!![i])) {
                    mMediaFileData!!.add(mCheckMediaData!![i])
                }
            }
        }
        tv_title.text =
            getString(R.string.count_sum_count, (mPreviewPosition + 1).toString(), mMediaFileData!!.size.toString())
        updateFinishText()

        tv_selector.setCompoundDrawablesWithIntrinsicBounds(
            if (mMediaFileData!![mPreviewPosition].isCheck) R.mipmap.ic_preview_check else R.mipmap.ic_preview_uncheck,
            0,
            0,
            0
        )
        mPreviewAdapter = PreviewAdapter(mMediaFileData!!)
        vp_preview.adapter = mPreviewAdapter
        vp_preview.setCurrentItem(mPreviewPosition, true)
        vp_preview.setPageTransformer(true, PreviewAdapter.PreviewPageTransformer())

        mCheckAdapter = MediaCheckAdapter(this, mCheckMediaData!!)
        rv_check_media.adapter = mCheckAdapter
        mCheckAdapter.notifyCheckData(mMediaFileData!![mPreviewPosition])
        initAdapterEvent()
    }

    private fun initAdapterEvent() {
        vp_preview.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {

            }

            override fun onPageSelected(position: Int) {
                mPreviewPosition = position
                tv_title.text = getString(
                    R.string.count_sum_count,
                    (position + 1).toString(),
                    mMediaFileData!!.size.toString()
                )
                tv_selector.setCompoundDrawablesWithIntrinsicBounds(
                    if (mMediaFileData!![position].isCheck) R.mipmap.ic_preview_check else R.mipmap.ic_preview_uncheck,
                    0,
                    0,
                    0
                )
                mCheckAdapter.notifyCheckData(mMediaFileData!![mPreviewPosition])
                if (mCheckMediaData!!.contains(mMediaFileData!![mPreviewPosition])) {
                    rv_check_media.scrollToPosition(mCheckMediaData!!.indexOf(mMediaFileData!![mPreviewPosition]))
                }
            }

            override fun onPageScrollStateChanged(position: Int) {

            }
        })
        mPreviewAdapter.setOnPreviewViewClickListener(object : PreviewAdapter.OnPreviewViewClickListener {
            override fun onPreviewClick(view: View) {
                if (mAnimatorSet != null && mAnimatorSet!!.isRunning) {
                    mAnimatorSet!!.end()
                }
                setTitleViewAnimation()
                isShowTitleView = !isShowTitleView
            }
        })
        mPreviewAdapter.setOnPreviewVideoClickListener(object : PreviewAdapter.OnPreviewVideoClickListener {
            override fun onVideoClick(view: View, position: Int) {
                try {
                    val intent = Intent()
                    intent.setDataAndType(Uri.parse(mMediaFileData!![position].filePath), "video/*")
                    startActivityForResult(intent, Const.CODE_REQUEST_PRIVIEW_VIDEO)
                } catch (e: Exception) {
                    Toast.makeText(this@PreviewActivity, "没有默认播放器", Toast.LENGTH_SHORT).show()
                    mPreviewAdapter.mCbPlay.isChecked = false
                    mPreviewAdapter.notifyDataSetChanged()
                    e.printStackTrace()
                }
            }
        })
        mCheckAdapter.setOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (mMediaFileData!!.contains(mCheckMediaData!![position])) {
                    vp_preview.setCurrentItem(mMediaFileData!!.indexOf(mCheckMediaData!![position]), true)
                    mCheckAdapter.notifyDataSetChanged()
                }
            }
        })
    }

    /**
     * 设置toolbar隐藏和显示动画
     */
    private fun setTitleViewAnimation() {
        val topAnimatorTranslation: ObjectAnimator
        val bottomAnimatorTranslation: ObjectAnimator
        if (mAnimatorSet == null) {
            mAnimatorSet = AnimatorSet()
        }
        if (isShowTitleView) {
            Handler().postDelayed(Runnable {
                val params = root.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(
                    0,
                    ScreenUtil.getStatusBarHeight(this),
                    0,
                    0
                )
                root.layoutParams = params

                //使得布局延伸到状态栏和导航栏区域
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }, 200)
            topAnimatorTranslation = ObjectAnimator.ofFloat(
                toolbar,
                "translationY",
                0f,
                -(toolbar.measuredHeight).toFloat()
            )
            bottomAnimatorTranslation =
                ObjectAnimator.ofFloat(ll_bottom, "translationY", 0f, ll_bottom.measuredHeight.toFloat())
        } else {
            val params = root.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(
                0,
                0,
                0,
                0
            )
            root.layoutParams = params
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            topAnimatorTranslation = ObjectAnimator.ofFloat(
                toolbar,
                "translationY",
                -(toolbar.measuredHeight).toFloat(),
                0f
            )
            bottomAnimatorTranslation =
                ObjectAnimator.ofFloat(ll_bottom, "translationY", ll_bottom.measuredHeight.toFloat(), 0f)
        }
        mAnimatorSet!!.duration = 300
        mAnimatorSet!!.interpolator = LinearInterpolator()
        mAnimatorSet!!.playTogether(topAnimatorTranslation, bottomAnimatorTranslation)
        mAnimatorSet!!.playTogether(topAnimatorTranslation)
        mAnimatorSet!!.start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            0 -> {
                if (requestCode == Const.CODE_REQUEST_PRIVIEW_VIDEO) {
                    mPreviewAdapter.mCbPlay.isChecked = false
                    mPreviewAdapter.notifyDataSetChanged()
                }
            }
            Activity.RESULT_OK -> {
                if (requestCode == UCrop.REQUEST_CROP) {
                    if (data == null) {
                        return
                    }
                    val resultUri = UCrop.getOutput(data)
                    if (resultUri != null && resultUri.path != null) {
                        mCheckMediaData!!.clear()
                        val file = File(resultUri.path!!)
                        if (FileUtil.existsFile(file.absolutePath)) {
                            mCheckMediaData!!.add(MediaSelectorFile.selectThisFile(file))
                            EventBus.getDefault()
                                .post(MessageEvent(MessageEvent.HANDING_DATA_IN_PREVIEW_PAGE, mCheckMediaData))
                            finish()
                        } else {
                            Toast.makeText(this, R.string.file_not_exit, Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
            UCrop.RESULT_ERROR -> {
                if (requestCode == UCrop.REQUEST_CROP) {
                    Toast.makeText(this, R.string.crop_image_fail, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> onBackPressed()
            R.id.tv_finish -> {
                if (mCheckMediaData!!.size <= 0) {
                    mMediaFileData!![mPreviewPosition].isCheck = true
                    mCheckMediaData!!.add(mMediaFileData!![mPreviewPosition])
                }
                clickFinishEvent()
            }
            R.id.tv_selector -> {
                if (mCheckMediaData!!.size < mOptions.maxChooseMedia || mCheckMediaData!!.size == mOptions.maxChooseMedia && mMediaFileData!![mPreviewPosition].isCheck
                ) {
                    mMediaFileData!![mPreviewPosition].isCheck = !mMediaFileData!![mPreviewPosition].isCheck
                    tv_selector.setCompoundDrawablesWithIntrinsicBounds(
                        if (mMediaFileData!![mPreviewPosition].isCheck) R.mipmap.ic_preview_check else R.mipmap.ic_preview_uncheck,
                        0,
                        0,
                        0
                    )
                    EventBus.getDefault()
                        .post(MessageEvent(MessageEvent.SELECTOR_IN_PREVIEW_PAGE, mMediaFileData!![mPreviewPosition]))
                    if (mMediaFileData!![mPreviewPosition].isCheck) {
                        mCheckAdapter.addItemNotifyData(mMediaFileData!![mPreviewPosition])
                        rv_check_media.scrollToPosition(mCheckMediaData!!.indexOf(mMediaFileData!![mPreviewPosition]))
                    } else {
                        if (mCheckMediaData!!.contains(mMediaFileData!![mPreviewPosition])) {
                            mCheckAdapter.removeItemNotifyData(
                                mCheckMediaData!!.indexOf(
                                    mMediaFileData!![mPreviewPosition]
                                )
                            )
                            rv_check_media.scrollToPosition(mCheckMediaData!!.size - 1)
                        }
                    }
                    //设置完成的数量
                    updateFinishText()
                } else {
                    Toast.makeText(
                        this, getString(
                            R.string.max_choose_media,
                            mOptions.maxChooseMedia.toString()
                        ), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * 更新完成按钮的文本信息
     */
    private fun updateFinishText() {
        if (mCheckMediaData!!.size > 0) {
            tv_finish.isEnabled = true
            tv_finish.text = getString(
                R.string.finish_count,
                mCheckMediaData!!.size.toString(),
                mOptions.maxChooseMedia.toString()
            )
        } else {
            tv_finish.isEnabled = false
            tv_finish.setText(R.string.finish)
        }
    }

    /**
     * 点击完成按钮事件
     */
    private fun clickFinishEvent() {
        if (mOptions.isCompress && !mOptions.isShowVideo && !mOptions.isCrop) {
            val viewGroup = window.decorView as ViewGroup
            val inflate =
                LayoutInflater.from(this@PreviewActivity).inflate(R.layout.item_loading_view, viewGroup, false)
            compressImage(mCheckMediaData!!, object : ImageCompress.OnCompressImageListCallback{
                override fun onCompressError(errorMsg: String) {
                    if (viewGroup.indexOfChild(inflate) != -1) {
                        viewGroup.removeView(inflate)
                    }
                }

                override fun onCompressSuccess(fileList: List<File>) {
                    mCheckMediaData!!.clear()
                    for (file in fileList) {
                        mCheckMediaData!!.add(MediaSelectorFile.selectThisFile(file))
                    }
                    EventBus.getDefault().post(MessageEvent(MessageEvent.HANDING_DATA_IN_PREVIEW_PAGE, mCheckMediaData))
                    finish()
                    if (viewGroup.indexOfChild(inflate) != -1) {
                        viewGroup.removeView(inflate)
                    }
                }

                override fun onStartCompress() {
                    viewGroup.addView(inflate)
                }
            })
        } else {
            if (mOptions.isCrop && mOptions.maxChooseMedia == 1) {
                if (!mCheckMediaData!![0].isVideo) {
                    val options = UCrop.Options()
                    options.setCompressionQuality(100)
                    options.setToolbarColor(ContextCompat.getColor(this, mOptions.themeColor))
                    options.setStatusBarColor(ContextCompat.getColor(this, mOptions.themeColor))
                    options.setLogoColor(ContextCompat.getColor(this, mOptions.themeColor))
                    options.setActiveWidgetColor(ContextCompat.getColor(this, mOptions.themeColor))
                    UCrop.of(
                        Uri.fromFile(File(mCheckMediaData!![0].filePath)),
                        Uri.fromFile(FileUtil.createImageFile(this, "Crop"))
                    )
                        .withAspectRatio(mOptions.scaleX.toFloat(), mOptions.scaleY.toFloat())
                        .withMaxResultSize(mOptions.cropWidth, mOptions.cropHeight)
                        .withOptions(options)
                        .start(this)
                } else {
                    Toast.makeText(this, R.string.video_not_crop, Toast.LENGTH_SHORT).show()
                }
            } else {
                EventBus.getDefault().post(MessageEvent(MessageEvent.HANDING_DATA_IN_PREVIEW_PAGE, mCheckMediaData))
                finish()
            }
        }
    }

    /**
     * 压缩图片
     */
    private fun compressImage(
        mMediaFileData: List<MediaSelectorFile>,
        callback: ImageCompress.OnCompressImageListCallback
    ) {
        val configData = ArrayList<ImageConfig>()
        for (i in mMediaFileData.indices) {
            configData.add(ImageConfig.getDefaultConfig(mMediaFileData[i].filePath!!))
        }
        ImageCompress.get().compress(this, configData, callback)
    }

    override fun onPause() {
        super.onPause()
        if (mAnimatorSet != null && mAnimatorSet!!.isRunning && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mAnimatorSet!!.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAnimatorSet != null) {
            mAnimatorSet!!.end()
        }
    }
}