package com.aihuishou.hackserver.core.vpn

import android.content.Intent
import android.net.VpnService
import com.aihuishou.hackserver.core.HackServer
import com.aihuishou.hackserver.core.func.LogFunc
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ConvertUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object VpnFunc {
    @JvmStatic
    fun queryVpnStatus(): Int {
        return if(MyVpnService.isStarted) {
            0
        } else {
            1
        }
    }

    @JvmStatic
    fun startup(): Int {
        if(HackServer.coreApplication == null) {
            return -1
        }
        val intent = VpnService.prepare(HackServer.coreApplication)
        return if(intent != null) {
            ActivityUtils.getTopActivity().startActivityForResult(intent, 9999)
            -2
        } else {
            if(!MyVpnService.isStarted) {
                HackServer.coreApplication!!.startService(
                    Intent(
                        HackServer.coreApplication,
                        MyVpnService::class.java
                    )
                )
                MyVpnService.createDataFile(HackServer.coreApplication!!)
            }
            0
        }
    }

    @JvmStatic
    fun shutdown(): Int {
        return if(HackServer.coreApplication == null) {
            -1
        } else {
            HackServer.coreApplication!!.stopService(
                Intent(
                    HackServer.coreApplication!!,
                    MyVpnService::class.java
                )
            )
            0
        }
    }

    @JvmStatic
    fun getVpnPackageFilePath(): String {
        val filePath = MyVpnService.packageDataPath

        val file = File(filePath)
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

    private fun createDownloadRef(file: File): String{
        return "./files/download?filePath=${file.absolutePath}"
    }

    private fun createViewRef(file: File): String{
        return "./files/view?filePath=${file.absolutePath}"
    }

}