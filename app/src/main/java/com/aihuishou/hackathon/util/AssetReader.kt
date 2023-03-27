package com.aihuishou.hackathon.util

import com.aihuishou.hackathon.application.HackApplication
import java.io.InputStream

object AssetReader {

    private fun readFromInputStream(inputStream: InputStream): String {
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
    fun readFromAsset(assetFileName: String): String? {
        var jsContent = ""
        try {
            val inputStream = HackApplication.instance.assets.open(assetFileName)
            jsContent = readFromInputStream(inputStream)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return jsContent
    }
}