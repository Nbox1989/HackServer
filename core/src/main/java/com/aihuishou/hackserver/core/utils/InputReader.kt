package com.aihuishou.hackserver.core.utils

import java.io.File
import java.io.InputStream

object InputReader {

    @JvmStatic
    fun readFromInputStream(inputStream: InputStream): String {
        var jsContent = ""
        try {
            inputStream.bufferedReader().useLines { lines ->
                jsContent = lines.joinToString(separator="")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsContent
    }


    @JvmStatic
    fun readFormFile(file: File): String {
        if(file.length() > 100 * 1000 * 1000) {
            return "文件大小超过100M，请下载后查看内容。"
        } else {
            return file.readText()
        }
    }
}