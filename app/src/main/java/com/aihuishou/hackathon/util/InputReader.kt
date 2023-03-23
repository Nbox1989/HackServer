package com.aihuishou.hackathon.util

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

}