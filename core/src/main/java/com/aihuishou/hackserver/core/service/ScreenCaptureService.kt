package com.aihuishou.hackserver.core.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import androidx.core.app.NotificationCompat
import com.aihuishou.hackserver.core.R
import com.aihuishou.hackserver.core.utils.scopeIo
import java.io.File
import java.nio.ByteBuffer

/**
 * 屏幕截屏/录屏服务
 */
class ScreenCaptureService : Service() {

    private val TAG = "ScreenShortRecordService"

    private val NOTIFICATION_CHANNEL_ID = "com.aihuishou.hackserver.MediaService"
    private val NOTIFICATION_CHANNEL_NAME = "com.aihuishou.hackserver.channel_name"
    private val NOTIFICATION_CHANNEL_DESC = "com.aihuishou.hackserver.channel_desc"

    private var mMediaProjectionManager: MediaProjectionManager? = null
    private var mMediaProjection: MediaProjection? = null

    //截图
    private var imageReader: ImageReader? = null
    private var callback: ScreenshotListener? = null
    private var isGot: Boolean = false

    //录屏
    private var recorder: MediaRecorder? = null
    private var virtualDisplay: VirtualDisplay? = null

    /**
     * ScreenShortBinder 为客户端提供 getService() 方法，用于获取 ScreenShortService 的当前实例。
     * 这样，客户端便可调用服务中的公共方法
     */
    inner class ScreenShortBinder : Binder() {
        fun getService(): ScreenCaptureService = this@ScreenCaptureService
    }

    override fun onBind(intent: Intent): IBinder {
        return ScreenShortBinder()
    }

    fun startShort(intent: Intent, callback: ScreenshotListener) {
        //开启通知，并申请成为前台服务
        startNotification()
        //标记
        this.isGot = false
        //回调
        this.callback = callback

        mMediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        //获取令牌
        mMediaProjection = mMediaProjectionManager?.getMediaProjection(Activity.RESULT_OK, intent)

        //这里延迟一会再取
        Handler(Looper.myLooper()!!).postDelayed(object : Runnable {
            override fun run() {
                //配置ImageReader
                configImageReader()
            }
        }, 400)
    }

    fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationIntent = Intent(this, ScreenCaptureService::class.java)

            val pendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_IMMUTABLE
                    );
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                    );
                }
            val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.mipmap.ic_launcher
                    )
                )
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Starting Service")
                .setContentText("Starting monitoring service")
                .setContentIntent(pendingIntent)
            val notification: Notification = notificationBuilder.build()
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = NOTIFICATION_CHANNEL_DESC
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            //申请成为前台应用
            startForeground(
                1,
                notification
            ) //必须使用此方法显示通知，不能使用notificationManager.notify，否则还是会报错误
            //java.lang.SecurityException: Media projections require a foreground service of type ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
        }
    }

    @SuppressLint("WrongConstant")
    fun configImageReader() {
        val dm = resources.displayMetrics
        imageReader = ImageReader.newInstance(
            dm.widthPixels, dm.heightPixels,
            PixelFormat.RGBA_8888, 1
        ).apply {
            setOnImageAvailableListener({
                //这里页面帧发生变化时就会回调一次，我们只需要获取一张图片，加个标记位，避免重复
                if (!isGot) {
                    isGot = true
                    //这里就可以保存图片了
                    savePicTask(it)
                }
            }, null)

            //把内容投射到ImageReader 的surface
            mMediaProjection?.createVirtualDisplay(
                TAG, dm.widthPixels, dm.heightPixels, dm.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, surface, null, null
            )
        }
    }

    /**
     * 保存图片
     */
    private fun savePicTask(reader: ImageReader) {
        scopeIo {
            var image: Image? = null
            try {
                //获取捕获的照片数据
                image = reader.acquireLatestImage()
                val width = image.width
                val height = image.height
                //拿到所有的 Plane 数组
                val planes = image.planes
                val plane = planes[0]

                val buffer: ByteBuffer = plane.buffer
                //相邻像素样本之间的距离，因为RGBA，所以间距是4个字节
                val pixelStride = plane.pixelStride
                //每行的宽度
                val rowStride = plane.rowStride
                //因为内存对齐问题，每个buffer 宽度不同，所以通过pixelStride * width 得到大概的宽度，
                //然后通过 rowStride 去减，得到大概的内存偏移量，不过一般都是对齐的。
                val rowPadding = rowStride - pixelStride * width
                // 创建具体的bitmap大小，由于rowPadding是RGBA 4个通道的，所以也要除以pixelStride，得到实际的宽
                val bitmap = Bitmap.createBitmap(
                    width + rowPadding / pixelStride,
                    height, Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)
                callback?.onScreenSuc(bitmap)
                //服务退出前台
                stopForeground(true)
                mMediaProjection?.stop()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                //记得关闭 image
                try {
                    image?.close()
                } catch (e: Exception) {
                }
            }
        }
    }

    //停止录制
    fun stopRecorder() {
        recorder?.stop()
        recorder?.release()
        recorder = null

        mMediaProjection?.stop()

        //退出前台服务
        stopForeground(true)
    }

    //开始录屏
    fun startRecorder(path: String, fileName: String, intent: Intent) {
        //开启通知，并申请成为前台服务
        startNotification()
        this.isGot = false
        this.callback = callback

        mMediaProjectionManager =
            getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        //获得令牌
        mMediaProjection = mMediaProjectionManager?.getMediaProjection(Activity.RESULT_OK, intent)

        //这里延迟一会再取
        Handler(Looper.myLooper()!!).postDelayed(object : Runnable {
            override fun run() {
                //配置MediaRecorder
                if (configMediaRecorder(path, fileName)) {
                    try {
                        //开始录屏
                        recorder?.start()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }, 400)
    }

    /**
     * 配置MediaProjection
     */
    private fun configMediaRecorder(path: String, fileName: String): Boolean {

        //创建文件夹
        val dir = File(path)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(path, fileName)
        if (file.exists()) {
            file.delete()
        }
        val dm = resources.displayMetrics
        recorder = MediaRecorder()
        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC) //音频载体
            setVideoSource(MediaRecorder.VideoSource.SURFACE) //视频载体
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4) //输出格式
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) //音频格式
            setVideoEncoder(MediaRecorder.VideoEncoder.H264) //视频格式
            setVideoSize(dm.widthPixels, dm.heightPixels) //size
            setVideoFrameRate(30) //帧率
            setVideoEncodingBitRate(3 * 1024 * 1024) //比特率
            //设置文件位置
            setOutputFile(file.absolutePath)
            try {
                prepare()
                virtualDisplay = mMediaProjection?.createVirtualDisplay(
                    TAG,
                    dm.widthPixels,
                    dm.heightPixels,
                    dm.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    surface,
                    null,
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
        return true
    }


    override fun onUnbind(intent: Intent?): Boolean {
        mMediaProjection?.stop()
        imageReader?.close()
        try {
            recorder?.stop()
            recorder?.release()
            recorder = null
        } catch (e: Exception) {
        }
        return super.onUnbind(intent)
    }
}

interface ScreenshotListener {

    suspend fun onScreenSuc(bitmap:Bitmap)

}