package com.aihuishou.hackserver.core.utils

import com.aihuishou.hackserver.core.HackServer
import java.io.InputStream

object AssetReader {

    private fun readFromInputStream(inputStream: InputStream): String {
        var jsContent = ""
        try {
            inputStream.bufferedReader().useLines { lines ->
                jsContent = lines.joinToString(separator="\n")
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
            if(HackServer.coreApplication == null) {
                jsContent = "";
            } else {
                val inputStream = HackServer.coreApplication!!.assets.open(assetFileName)
                jsContent = readFromInputStream(inputStream)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return jsContent
    }
}