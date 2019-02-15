package com.hzy.selector

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hzy.selector.adapter.MediaFileAdapter
import com.hzy.selector.bean.MediaSelectorFile
import com.hzy.selector.bean.MediaSelectorFolder
import com.hzy.selector.eventbus.MessageEvent
import com.hzy.selector.listener.OnRecyclerItemClickListener
import com.hzy.selector.resolver.Const
import com.hzy.selector.resolver.LoadMediaCallback
import com.hzy.selector.resolver.MediaHelper
import com.hzy.selector.util.FileUtil
import com.hzy.selector.util.StatusBarUtil
import com.hzy.selector.widget.FolderWindow
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity
import kotlinx.android.synthetic.main.activity_media_selector.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import utils.bean.ImageConfig
import utils.task.CompressImageTask
import java.io.File
import java.util.*


/**
 *
 * @author: ziye_huang
 * @date: 2019/2/11
 */
class MediaSelectorActivity : AppCompatActivity(), View.OnClickListener {

    private var mMediaFileAdapter: MediaFileAdapter? = null
    private lateinit var mMediaFileData: MutableList<MediaSelectorFile>
    private lateinit var mCheckMediaFileData: MutableList<MediaSelectorFile>
    private var mMediaFolderData: MutableList<MediaSelectorFolder>? = null
    private var mFolderWindow: FolderWindow? = null
    private lateinit var mOptions: MediaSelector.MediaOptions
    //    private lateinit var mCameraFile: File
//    private var mTvTop: TextView
//    private var mTvBottom: TextView
    private var mStatusBarColor = R.color.status_bar_color
    private var mCameraFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setStatusBarColor(this, ContextCompat.getColor(this, mStatusBarColor))
        setContentView(R.layout.activity_media_selector)
        setSupportActionBar(toolbar)

        initView()
        initOptions()
        initData()
        initEvent()
    }

    fun initView() {
        EventBus.getDefault().register(this)
        iv_back.setOnClickListener(this)
        tv_finish.setOnClickListener(this)
//        tv_dictory.setOnClickListener(this)
        rl_dir.setOnClickListener(this)
        tv_preview.setOnClickListener(this)
        recyclerView.layoutManager = GridLayoutManager(this, 4)
    }

    fun initData() {
        val mediaHelper = MediaHelper(this)
        mCheckMediaFileData = mutableListOf()
        if (null == mMediaFileAdapter) {
            mMediaFileAdapter = MediaFileAdapter(this, mMediaFileData, mOptions)
            recyclerView.adapter = mMediaFileAdapter
        }
        mediaHelper.loadMedia(mOptions.isShowCamera, mOptions.isShowVideo, object : LoadMediaCallback {
            override fun onLoadMediaFinish(data: MutableList<MediaSelectorFolder>) {
                if (data.size > 0) {
                    mMediaFileData.addAll(data[0].fileData)
                    if (null == mMediaFolderData) {
                        mMediaFolderData = data
                    } else {
                        mMediaFolderData!!.addAll(data)
                    }
                    mMediaFileAdapter?.notifyDataSetChanged()
                }
            }
        })
    }

    /**
     * 初始化 MediaOptions
     */
    private fun initOptions() {
        mMediaFileData = mutableListOf()
        mOptions = intent.getParcelableExtra(Const.KEY_OPEN_MEDIA)
        if (mOptions.maxChooseMedia <= 0) {
            mOptions.maxChooseMedia = 1
        }
    }

    private fun initEvent() {
        mMediaFileAdapter?.setOnRecyclerItemClickListener(object : OnRecyclerItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (mMediaFileData[position].isShowCamera) {
                    openCamera()
                } else {
                    if (mOptions.isCrop && mOptions.maxChooseMedia === 1 && mOptions.isShowVideo && mMediaFileData[position].isVideo) run {
                        Toast.makeText(this@MediaSelectorActivity, R.string.video_not_crop, Toast.LENGTH_SHORT).show()
                    } else {
                        toPreviewActivity(position, mMediaFileData, mCheckMediaFileData)
                    }
                }
            }
        })

        mMediaFileAdapter?.setOnCheckedMediaListener(object : MediaFileAdapter.OnCheckedMediaListener {
            override fun onChecked(isCheck: Boolean, position: Int) {
                if (isCheck) {
                    mMediaFileData[position].isCheck = false
                    mCheckMediaFileData.remove(mMediaFileData[position])
                } else {
                    if (mCheckMediaFileData.size < mOptions.maxChooseMedia) {
                        mMediaFileData[position].isCheck = true
                        mCheckMediaFileData.add(mMediaFileData[position])
                    } else {
                        Toast.makeText(
                            this@MediaSelectorActivity,
                            getString(R.string.max_choose_media, mOptions.maxChooseMedia.toString()),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                updateCheckedStatus()
                mMediaFileAdapter!!.notifyItemChanged(position)
            }
        })
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == 0) {
                    Glide.with(this@MediaSelectorActivity).resumeRequests()
                } else {
                    Glide.with(this@MediaSelectorActivity).pauseRequests()
                }
            }
        })
    }

    /**
     * 打开相机
     */
    fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //  cameraIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            mCameraFile = FileUtil.createImageFile(this@MediaSelectorActivity)
            val cameraUri = FileUtil.fileToUri(this@MediaSelectorActivity, mCameraFile!!, cameraIntent)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
            startActivityForResult(cameraIntent, Const.REQUEST_CAMERA_CODE)
        }
    }

    /**
     * 更新选中的状态
     */
    fun updateCheckedStatus() {
        if (mCheckMediaFileData.size > 0) {
            tv_finish.isEnabled = true
            tv_finish.text = getString(
                R.string.finish_count,
                mCheckMediaFileData.size.toString(),
                mOptions.maxChooseMedia.toString()
            )
            tv_preview.text = getString(R.string.preview_count, mCheckMediaFileData.size.toString())
        } else {
            tv_finish.isEnabled = false
            tv_finish.text = getString(R.string.finish)
            tv_preview.text = getString(R.string.preview)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_back -> onBackPressed()
            R.id.tv_finish -> {
                resultMediaData()
            }
            R.id.rl_dir -> {
                showSelectMediaFolderWindow(v)
            }
            R.id.tv_preview -> {
                if (mCheckMediaFileData.size > 0) {
                    toPreviewActivity(0, mCheckMediaFileData, mCheckMediaFileData)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == Const.REQUEST_CAMERA_CODE) {
                    if (FileUtil.existsFile(mCameraFile!!.absolutePath)) {
                        FileUtil.scanImage(this, mCameraFile!!)
                        val mediaSelectorFile = MediaSelectorFile.checkFileToThis(mCameraFile!!)
                        if (mediaSelectorFile.hasData()) {
                            mCheckMediaFileData.add(mediaSelectorFile)
                        }
                        resultMediaData()
                    }
                }
            }
        }
    }

    private fun resultMediaData() {
        if (mCheckMediaFileData.size > 0) {
            if (mOptions.isCompress && !mOptions.isShowVideo) {
                val viewGroup = window.decorView as ViewGroup
                val inflate =
                    LayoutInflater.from(this@MediaSelectorActivity)
                        .inflate(R.layout.item_loading_view, viewGroup, false)
                compressImage(mCheckMediaFileData, object : CompressImageTask.OnImagesResult {
                    override fun startCompress() {
                        viewGroup.addView(inflate)
                    }

                    override fun resultFilesSucceed(list: List<File>) {
                        mCheckMediaFileData.clear()
                        for (file in list) {
                            mCheckMediaFileData.add(MediaSelectorFile.checkFileToThis(file))
                        }
                        resultMediaIntent()
                        if (viewGroup.indexOfChild(inflate) != -1) {
                            viewGroup.removeView(inflate)
                        }
                    }

                    override fun resultFilesError() {
                        if (viewGroup.indexOfChild(inflate) != -1) {
                            viewGroup.removeView(inflate)
                        }
                    }
                })
            } else {
                resultMediaIntent()
            }
        }
    }

    /**
     * 把数据返回给调用者
     */
    fun resultMediaIntent() {
        val intent = Intent()
        intent.putParcelableArrayListExtra(
            Const.KEY_REQUEST_MEDIA_DATA,
            mCheckMediaFileData as ArrayList<out Parcelable>
        )
        setResult(Const.CODE_RESULT_MEDIA, intent)
        finish()
    }

    /**
     * 压缩图片
     */
    private fun compressImage(
        mMediaFileData: List<MediaSelectorFile>,
        onImagesResult: CompressImageTask.OnImagesResult
    ) {
        val configData = ArrayList<ImageConfig>()
        for (i in mMediaFileData.indices) {
            configData.add(MediaSelectorFile.thisToDefaultImageConfig(mMediaFileData[i]))
        }
        CompressImageTask.get().compressImages(this as RxAppCompatActivity, configData, onImagesResult)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    fun onMessageEvent(event: MessageEvent) {
        when (event.getEventType()) {
            MessageEvent.HANDING_DATA_IN_PREVIEW_PAGE -> {
                var checkMediaData = event.getData() as MutableList<MediaSelectorFile>
                if (checkMediaData.size > 0) {
                    mCheckMediaFileData.clear()
                    mCheckMediaFileData.addAll(checkMediaData)
                    resultMediaIntent()
                }
            }
            MessageEvent.SELECTOR_IN_PREVIEW_PAGE -> {
                val mediaSelectorFile = event.getData() as MediaSelectorFile
                if (mediaSelectorFile.isCheck) {
                    //首先先判断选择的媒体库
                    if (!mCheckMediaFileData.contains(mediaSelectorFile)) {
                        mCheckMediaFileData.add(mediaSelectorFile)
                    }
                } else {
                    if (mCheckMediaFileData.contains(mediaSelectorFile)) {
                        mCheckMediaFileData.remove(mediaSelectorFile)
                    }
                }
                for (i in mMediaFolderData!!.indices) {
                    if (mMediaFolderData!![i].fileData.contains(mediaSelectorFile)) {
                        mMediaFolderData!![i].fileData[mMediaFolderData!![i].fileData.indexOf(mediaSelectorFile)].isCheck =
                            mediaSelectorFile.isCheck
                    }
                }
                updateCheckedStatus()
                mMediaFileAdapter!!.notifyDataSetChanged()
            }
        }
    }

    /**
     * 跳转到预览页面
     */
    fun toPreviewActivity(
        position: Int,
        data: MutableList<MediaSelectorFile>,
        checkData: MutableList<MediaSelectorFile>
    ) {
        val intent = Intent(this, PreviewActivity::class.java)
        intent.putParcelableArrayListExtra(Const.KEY_PREVIEW_DATA_MEDIA, data as ArrayList<out Parcelable>)
        intent.putParcelableArrayListExtra(
            Const.KEY_PREVIEW_CHECK_MEDIA,
            checkData as ArrayList<out Parcelable>
        )
        intent.putExtra(Const.KEY_OPEN_MEDIA, mOptions)
        intent.putExtra(Const.KEY_PREVIEW_POSITION, position)
        startActivity(intent)
    }

    /**
     * 显示选择目录对话框
     */
    private fun showSelectMediaFolderWindow(view: View) {
        when {
            mFolderWindow == null -> {
                mFolderWindow = FolderWindow(this, mMediaFolderData!!)
                mFolderWindow!!.setOnPopupItemClickListener(object : FolderWindow.OnPopupItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        selectFolder(position)
                    }
                })
                mFolderWindow!!.showWindow(view)
            }
            mFolderWindow!!.getFolderWindow().isShowing -> mFolderWindow!!.dismissWindows()
            else -> mFolderWindow!!.showWindow(view)
        }
    }

    /**
     * 选择目录
     */
    private fun selectFolder(position: Int) {
        tv_dictory.text = mMediaFolderData!![position].folderName
        mMediaFileData.clear()
        mMediaFileData.addAll(mMediaFolderData!![position].fileData)
        mMediaFileAdapter!!.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onBackPressed() {
        if (mFolderWindow != null && mFolderWindow!!.getFolderWindow().isShowing) {
            mFolderWindow!!.dismissWindows()
        } else {
            super.onBackPressed()
        }
    }
}