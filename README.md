
### MediaSelector

MediaSelector 高仿微信图片选择器，目前支持拍照、图片或视频单选、多选，多文件夹切换、图片预览及图片剪切等功能。


### 工具特点


- 可扩展能性强（支持可选文件个数、是否显示拍照等）
- 支持多图压缩
- 支持加载视频资源（加载视频资源，不会压缩）
- 使用方便，代码简洁

### 效果预览
![效果预览](https://raw.githubusercontent.com/huangziye/MediaSelector/master/screenshot/MediaSelector.gif)





### 添加 `MediaSelector` 到项目

- 第一步： 添加 `JitPack` 到项目的根 `build.gradle` 中


```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

- 第二步：添加库依赖


```gradle
dependencies {
    implementation 'com.github.huangziye:MediaSelector:${latest_version}'
}
```



```kotlin
 // 自定义选择图片方式
 val options = MediaSelector.MediaOptions()
 //是否要显示拍照功能
 options.isShowCamera = true
 //是否要压缩
 options.isCompress = false
 //是否要显示视频文件
 options.isShowVideo = false
 //设置要不要裁剪（视频不裁剪、单图选择接受裁剪，裁剪大小自己可以设置）
  options.isCrop = true
  //默认最多可选9张
  options.maxChooseMedia = 9
 
 //打开选择页面
 MediaSelector.with(this).setMediaOptions(options).openMediaSelectorActivity()
 
 
 
// 默认选择图片方式
 MediaSelector.with(this).openMediaSelectorActivity()


/**
* 结果回调事件
*/
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
```




<br />

### 关于我


- [简书](https://user-gold-cdn.xitu.io/2018/7/26/164d5709442f7342)

- [掘金](https://juejin.im/user/5ad93382518825671547306b)

- [Github](https://github.com/huangziye)

<br />

### License

```
Copyright 2018, huangziye

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```