package com.aihuishou.hackathon

import android.app.Activity
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ScreenRecordActivity: ComponentActivity() {

    companion object {
        private const val REQUEST_CODE_SCREEN_CAPTURE = 1009
    }

    private val isRecordingLiveData = MutableLiveData(false)

    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null

    private var width = 0
    private var height = 0
    private var density = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        width = resources.displayMetrics.widthPixels
        height = resources.displayMetrics.heightPixels
        density = resources.displayMetrics.densityDpi

        setContent {
            ServerContent()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
    }

    private fun startRecordRequest() {
        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = mediaProjectionManager?.createScreenCaptureIntent()
        startActivityForResult(intent, REQUEST_CODE_SCREEN_CAPTURE)
    }

    private fun initRecord() {
        mediaRecorder?.release()

        val folderPath = filesDir.absolutePath + "/hackRecord"
        val folder = File(folderPath)
        if(!folder.exists()) {
            folder.mkdirs()
        }
        val filePath = "$folderPath/" + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(Date(System.currentTimeMillis())) + ".mp4"
        mediaRecorder = MediaRecorder()
        //设置录像机的一系列参数
        //设置音频来源
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频来源
        mediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //设置视频格式为mp4
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //设置视频存储地址，返回的文件夹下的命名为当前系统事件的文件
        //保存在该位置
        mediaRecorder!!.setOutputFile(filePath);
        //设置视频大小，清晰度
        mediaRecorder!!.setVideoSize(1080, 1920);
        //设置视频编码为H.264
        mediaRecorder!!.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置音频编码
        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置视频码率
        mediaRecorder!!.setVideoEncodingBitRate(2 * 1920 * 1080);
        mediaRecorder!!.setVideoFrameRate(30);

//        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
//        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
//        mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
//        mediaRecorder!!.setOutputFile(filePath)
//        mediaRecorder!!.setOnErrorListener { _, what, extra ->
//            Log.i("TAG", "onError what = $what, extra = $extra")
//        }


        try {
            mediaRecorder!!.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun startRecord() {
        mediaRecorder?.start()
        isRecordingLiveData.value = true
    }

    private fun createVirtualDisplay() {
        //虚拟屏幕通过MediaProjection获取，传入一系列传过来的参数
        //可能创建时会出错，捕获异常
        try {
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "VirtualScreen",
                width,
                height,
                density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mediaRecorder?.surface,
                null,
                null)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun stopRecord() {
        try{
            //Recorder停止录像，重置还原，以便下一次使用
            mediaRecorder?.stop()
            mediaRecorder?.reset()
            mediaRecorder?.release()
            //释放virtualDisplay的资源
            virtualDisplay?.release()
        }catch (e: Exception){
            e.printStackTrace();
        }
        isRecordingLiveData.value = false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SCREEN_CAPTURE && resultCode == Activity.RESULT_OK) {
            if(data != null) {
                mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)
                initRecord()
                createVirtualDisplay()
                startRecord()
            }
        }
    }

    @Composable
    fun ServerContent() {
        Box{
            val isRecording by isRecordingLiveData.observeAsState()
            Text(
                text = if(isRecording == true) "停止录制" else "开始录制",
                fontSize = 40.sp,
                modifier = Modifier
                    .wrapContentSize()
                    .align(Alignment.Center)
                    .clickable {
                        if(isRecording == true) {
                            stopRecord()
                        } else {
                            startRecordRequest()
                        }
                    },
            )
        }
    }
}