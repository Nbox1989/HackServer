package com.aihuishou.hackserver.core.func

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.aihuishou.hackserver.core.HackServer
import com.blankj.utilcode.util.ConvertUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileListFunc {

    fun getFilesString(root: String, path: String): String{

        val file = File(root + path)//HackApplication.instance.dataDir//File(path)
        val subFiles = file.listFiles()?.toMutableList()?: emptyList()
        if(subFiles.isEmpty()) {
            return createEmptyFileList()
        }

        val sorted = subFiles.sortedWith { o1, o2 ->
            if (o1.isDirectory && o2.isDirectory) {
                o1.name.compareTo(o2.name)
            } else if (!o1.isDirectory && !o2.isDirectory) {
                o1.name.compareTo(o2.name)
            } else if (o1.isDirectory) {
                -1
            } else {
                1
            }
        }
        val tags = sorted.map {
            if(it.isDirectory) {
                createDirectoryTag(it)
            } else {
                createFileTag(it)
            }
        }

        return tags.joinToString("")
    }

    private fun createEmptyFileList(): String {
        return "<div>[没有文件]</div>"
    }

    private fun createDirectoryTag(dir: File): String {
        val icon = "📂"
        val name = dir.name
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(Date(dir.lastModified()))
        return "<div onclick=\"openFolder('$name')\"><span>$icon</span> <b>$name</b> <span style=\"color:grey\">$time</span></div>"
    }

    private fun createFileTag(file: File): String {
        val icon = "📃"
        val name = file.name
        val size = ConvertUtils.byte2FitMemorySize(file.length(), 1)
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
            .format(Date(file.lastModified()))
        val txtFile = file.isTextFile()
        val apkFile = file.isApkFile()
        val dbFile = file.isDatabaseFile()
        return "<div>$icon <b>$name</b> <span style=\"color:blue\">$size</span> <span style=\"color:grey\">$time</span>  <a target=\"blank\" href=\"${createDownloadRef(file)}\">下载</a>" +
                if(txtFile) "  <a target=\"blank\" href=\"${createViewRef(file)}\">查看</a>" else "" +
                if(apkFile) "  <a target=\"blank\" href=\"${createInstallRef(file)}\">安装</a>" else "" +
                if(dbFile) "  <a target=\"blank\" href=\"${createDbManageRef(file)}\">管理</a>" else "" +
                "</div>"
    }

    private fun createDownloadRef(file: File): String{
        return "./download?filePath=${file.absolutePath}"
    }

    private fun createViewRef(file: File): String{
        return "./view?filePath=${file.absolutePath}"
    }

    private fun createInstallRef(file: File): String{
        return "./install?filePath=${file.absolutePath}"
    }

    private fun createDbManageRef(file: File): String {
        return "../database/view?path=${file.absolutePath}"
    }

    fun installApkFile(apkFilePath: File): Int {
        return if(HackServer.coreApplication != null) {
            val intent = Intent(Intent.ACTION_VIEW)
            val apkUri = FileProvider.getUriForFile(HackServer.coreApplication!!, "com.nbox.hackserver.provider", apkFilePath)
            intent.setDataAndType(
                apkUri,
                "application/vnd.android.package-archive"
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            HackServer.coreApplication!!.startActivity(intent)
            0
        } else {
            -1
        }
    }
}

private fun File.isTextFile(): Boolean {
    return this.name.endsWith("txt") ||
        this.name.endsWith("xml") ||
        this.name.endsWith("md") ||
        this.name.endsWith("json") ||
        this.name.endsWith("html") ||
        this.name.endsWith("rtf") ||
        this.name.endsWith("log") ||
        this.name.endsWith("ini") ||
        this.name.endsWith("conf") ||
        this.name.endsWith("yml") ||
        this.name.endsWith("yaml") ||
        this.name.endsWith("sh")
}

private fun File.isApkFile(): Boolean {
    return this.name.endsWith("apk")
}

private fun File.isDatabaseFile(): Boolean {
    return this.name.endsWith("db")
}