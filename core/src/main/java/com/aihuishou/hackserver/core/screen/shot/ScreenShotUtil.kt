package com.aihuishou.hackserver.core.screen.shot

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*

class ScreenShotUtil {
    private val SCREENCAP_NAME = "screencap"
    private val VIRTUAL_DISPLAY_FLAGS: Int =
        DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
    private val WIDTH = 720
    private val HEIGHT = 720

    private var mImageReader: ImageReader? = null
    private var mVirtualDisplay: VirtualDisplay? = null

    fun startScreenCapture(context: Context, mediaProjection: MediaProjection) {
        try {
            // 创建 ImageReader 对象
            mImageReader = ImageReader.newInstance(
                context.resources.displayMetrics.widthPixels,
                context.resources.displayMetrics.heightPixels,
                PixelFormat.RGBA_8888,
                1)

            mediaProjection.createVirtualDisplay(
                SCREENCAP_NAME,
                context.resources.displayMetrics.widthPixels,
                context.resources.displayMetrics.heightPixels,
                context.getResources().getDisplayMetrics().densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                mImageReader!!.getSurface(), null, null);


            mImageReader!!.setOnImageAvailableListener({
                // 获取 Image 对象
                val image: Image = mImageReader!!.acquireLatestImage()
                // 将 Image 转换为 Bitmap
                val bitmap = imageToBitmap(context, image)
                // 保存 Bitmap 到文件中
                bitmap?.let {
                    saveBitmap(context, bitmap)
                }
            }, null)


        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun imageToBitmap(context: Context, image: Image): Bitmap? {
        try {
            val planes: Array<Image.Plane> = image.getPlanes()
            val buffer: ByteBuffer = planes[0].getBuffer()
            val pixelStride: Int = planes[0].getPixelStride()
            val rowStride: Int = planes[0].getRowStride()
            val rowPadding = rowStride - pixelStride * context.resources.displayMetrics.widthPixels
            val bitmap: Bitmap =
                Bitmap.createBitmap(
                    context.resources.displayMetrics.widthPixels + rowPadding / pixelStride,
                    context.resources.displayMetrics.heightPixels,
                    Bitmap.Config.ARGB_8888
                )
            bitmap.copyPixelsFromBuffer(buffer)
            image.close()
            return bitmap
//            val bytes = ByteArray(buffer.capacity())
//            buffer.get(bytes)
//            val bitmapImage: Bitmap? = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
//            return bitmapImage
        }catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }


    private fun saveBitmap(context: Context, bitmap: Bitmap) {
        // 获取外部存储目录
        val folderPath = context.filesDir.absolutePath + "/hack/shots"
        val folder = File(folderPath)
        if(!folder.exists()) {
            folder.mkdirs()
        }
        val filePath = "$folderPath/" + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(Date(System.currentTimeMillis())) + ".jpg"

        // 创建文件对象
        val file = File(filePath)
        var out: FileOutputStream? = null
        try {
            // 创建文件输出流
            out = FileOutputStream(file)
            // 将位图压缩为 PNG 格式并写入文件
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (out != null) {
                try {
                    out.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}