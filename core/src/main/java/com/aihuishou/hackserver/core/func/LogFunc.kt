package com.aihuishou.hackserver.core.func

import android.os.Environment
import android.util.Log
import com.aihuishou.hackserver.core.HackServer
import com.blankj.utilcode.util.ConvertUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class LogFunc {

    companion object {
        var logProcess: Process? = null
        var logcatPath: String = ""
    }

    fun queryLogProcessStatus(): String {
        return if(logProcess == null) {
            ""
        } else {
            logcatPath
        }
    }

    fun executeLogcat() : String{
        if(HackServer.coreApplication == null) {
            return ""
        }
        if(logProcess != null) {
            return ""
        }
        try {
            logProcess = Runtime.getRuntime().exec("logcat")

            val folderPath = HackServer.coreApplication!!.getFilesDir().getAbsolutePath() + "/hackLog"
            val folder = File(folderPath)
            if(!folder.exists()) {
                folder.mkdirs()
            }
            val filePath = "$folderPath/" + SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(Date(System.currentTimeMillis())) + ".txt"
            logcatPath = filePath
            Thread{
                try {
                    val file = File(filePath)
                    val outputStream = FileOutputStream(file)
                    val bufferedReader = BufferedReader(
                        InputStreamReader(
                            logProcess!!.inputStream
                        )
                    )
                    var line: String?

                    do {
                        line = bufferedReader.readLine()
                        outputStream.write("$line\n".toByteArray())
                    } while (line != null)
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return logcatPath
    }

    fun shutdownLogcat() : String {
        return if(logProcess == null) {
            "出错了，请重试"
        } else {
            logProcess!!.destroy()
            logProcess = null
            val file = File(logcatPath)
            val icon = "📃"
            val name = file.name
            val size = ConvertUtils.byte2FitMemorySize(file.length(), 1)
            val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(Date(file.lastModified()))
            return "<div>$icon <b>$name</b> <span style=\"color:blue\">$size</span> <span style=\"color:grey\">$time</span>  <a target=\"blank\" href=\"${createDownloadRef(file)}\">下载</a>  <a target=\"blank\" href=\"${
                createViewRef(
                    file
                )
            }\" target=\"_blank\">查看</a></div>"
        }
    }

    private fun createDownloadRef(file: File): String{
        return "./files/download?filePath=${file.absolutePath}"
    }

    private fun createViewRef(file: File): String{
        return "./files/view?filePath=${file.absolutePath}"
    }
}