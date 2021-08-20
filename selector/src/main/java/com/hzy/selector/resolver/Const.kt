package com.hzy.selector.resolver

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/11
 */
object Const {
    val KEY_PREVIEW_DATA_MEDIA = "key_preview_data_media"
    val KEY_PREVIEW_CHECK_MEDIA = "key_preview_check_media"
    val KEY_PREVIEW_POSITION = "key_preview_position"
    val KEY_CLEAR_MEDIA_DATA = "key_clear_media_data"
    val REQUEST_CODE_MEDIA_TO_PREVIEW = 101
    val MAX_CHOOSE_MEDIA = 9
    val KEY_OPEN_MEDIA = "key_open_media"
    /**
     * 把照片和视频的处理结果返回给调用者的key
     */
    val KEY_REQUEST_MEDIA_DATA = "key_request_media_data"
    val CODE_REQUEST_MEDIA = 1011
    /**
     * 把照片和视频的处理结果返回给调用者
     */
    val CODE_RESULT_MEDIA = 1012
    val CODE_REQUEST_PRIVIEW_VIDEO = 1013
    val ALL_FILE = "全部文件"
    val ALL_VIDEO = "全部视频"
    val REQUEST_CAMERA_CODE = 2000

    /**
     * 选择图片底部预览及选择目录布局的高度
     */
    val DEFAULT_VIEW_HEIGHT = 55f


    /**
     * 缓存图片数据，防止在intent的传递数据大于1M报错的问题
     * android.os.TransactionTooLargeException data parcel size 1352104 bytes
     */
    val MEDIA_SELECTOR_FILE_NAME = "hzy_cache_media_file"
}