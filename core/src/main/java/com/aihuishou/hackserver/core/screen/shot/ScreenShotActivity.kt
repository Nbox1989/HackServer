package com.aihuishou.hackserver.core.screen.shot

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.MutableLiveData
import com.aihuishou.hackserver.core.R
import com.aihuishou.hackserver.core.service.ScreenCaptureService
import com.aihuishou.hackserver.core.service.ScreenshotListener

class ScreenShotActivity: ComponentActivity() {

    companion object {
        private const val REQUEST_CODE_SCREEN_SHOT = 1009
    }

    private var resumeFlag = false

    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var mediaRecorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null

    private var width = 0
    private var height = 0
    private var density = 0

    private var mScreenShortService: ScreenCaptureService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, iBinder: IBinder?) {
            if (iBinder is ScreenCaptureService.ScreenShortBinder) {
                //截屏
                mScreenShortService = iBinder.getService()

            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            //no-op
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.hack_core_activity_screen_shot)

        width = resources.displayMetrics.widthPixels
        height = resources.displayMetrics.heightPixels
        density = resources.displayMetrics.densityDpi

        findViewById<TextView>(R.id.tv_screen_shot).setOnClickListener {
            startRecordRequest()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaRecorder?.release()
    }

    override fun onStart() {
        super.onStart()
        // 绑定服务
        Intent(this, ScreenCaptureService::class.java)
            .also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
    }

    override fun onResume() {
        super.onResume()
        if (!resumeFlag) {
//            startRecordRequest()
            resumeFlag = true
        }
    }

    override fun onStop() {
        super.onStop()
        //解绑服务
        unbindService(connection)
    }


    private fun startRecordRequest() {
        mediaProjectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val intent = mediaProjectionManager?.createScreenCaptureIntent()
        startActivityForResult(intent, REQUEST_CODE_SCREEN_SHOT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SCREEN_SHOT && resultCode == Activity.RESULT_OK) {
//            if(data != null) {
//                mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data)
//                mediaProjection?.let {
//                    ScreenShotUtil().startScreenCapture(this, it)
//                }
//            }
            data?.let {
                mScreenShortService?.startShort(it, object : ScreenshotListener {
                    override suspend fun onScreenSuc(bitmap: Bitmap) {
                        //显示截图
                        //showScreenshort(bitmap)
                        runOnUiThread {
                            findViewById<ImageView>(R.id.iv_screen_shot).setImageBitmap(bitmap)
                        }
                    }
                })
            }
        }
    }

}