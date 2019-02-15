package com.hzy.selector.resolver

/**
 *
 * @author: ziye_huang
 * @date: 2019/2/11
 */
object Const {
    var KEY_PREVIEW_DATA_MEDIA = "key_preview_data_media"
    var KEY_PREVIEW_CHECK_MEDIA = "key_preview_check_media"
    var KEY_PREVIEW_POSITION = "key_preview_position"
    var KEY_CLEAR_MEDIA_DATA = "key_clear_media_data"
    var REQUEST_CODE_MEDIA_TO_PREVIEW = 101
    var MAX_CHOOSE_MEDIA = 9
    var KEY_OPEN_MEDIA = "key_open_media"
    /**
     * 把照片和视频的处理结果返回给调用者的key
     */
    var KEY_REQUEST_MEDIA_DATA = "key_request_media_data"
    var CODE_REQUEST_MEDIA = 1011
    /**
     * 把照片和视频的处理结果返回给调用者
     */
    var CODE_RESULT_MEDIA = 1012
    var CODE_REQUEST_PRIVIEW_VIDEO = 1013
    var ALL_FILE = "全部文件"
    var ALL_VIDEO = "全部视频"
    var REQUEST_CAMERA_CODE = 2000

    /**
     * 选择图片底部预览及选择目录布局的高度
     */
    var DEFAULT_VIEW_HEIGHT = 55f
}