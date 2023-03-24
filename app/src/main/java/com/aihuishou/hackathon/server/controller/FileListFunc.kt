package com.aihuishou.hackathon.server.controller

import com.aihuishou.hackathon.application.HackApplication
import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ReflectUtils
import java.io.File
import java.lang.reflect.Modifier
import java.text.SimpleDateFormat
import java.util.*

class FileListFunc {

    fun getFilesString(path: String): String{

        val simpleDateFormat = SimpleDateFormat("yyyy-mm-dd HH:MM:SS", Locale.CHINA)

        val file = HackApplication.instance.dataDir//File(path)
        val subFiles = file.listFiles()?.toMutableList()?: emptyList()
        subFiles.sortedWith { o1, o2 ->
            if (o1.isDirectory && o2.isDirectory) {
                o1.lastModified().compareTo(o2.lastModified())
            } else if (!o1.isDirectory && !o2.isDirectory) {
                o1.lastModified().compareTo(o2.lastModified())
            } else if (o1.isDirectory) {
                0
            } else {
                1
            }
        }
        val tags = subFiles.map {
            val icon = if(it.isDirectory) "📂" else "📃"
            val name = it.name
            val time = simpleDateFormat.format(Date(it.lastModified()))
            "<div>$icon $name $time</div>"
        }

        return tags.joinToString("")
    }

}