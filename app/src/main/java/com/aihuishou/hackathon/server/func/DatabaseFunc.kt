package com.aihuishou.hackathon.server.func

import com.aihuishou.hackathon.application.HackApplication
import com.blankj.utilcode.util.ConvertUtils
import java.io.File
import java.io.FileFilter
import java.text.SimpleDateFormat
import java.util.*

object DatabaseFunc {

    @JvmStatic
    fun listDbHomeFiles(): String {
        val filesDir = HackApplication.instance.getDatabasePath("1").parentFile
        val dbFiles = filesDir?.listFiles { pathname -> pathname?.isFile == true && pathname.name.endsWith(".db") }

        return if(dbFiles.isNullOrEmpty()) {
            "<div style=\"color: red\">[没有数据库文件]</div>"
        } else {
            dbFiles.joinToString("") { file ->
                val icon = "📃"
                val name = file.name
                val size = ConvertUtils.byte2FitMemorySize(file.length(), 1)
                val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                    .format(Date(file.lastModified()))
                "<div>$icon <b>$name</b> <span style=\"color:blue\">$size</span> <span style=\"color:grey\">$time</span>  <a target=\"blank\" href=\"${
                    createViewRef(
                        file
                    )
                }\">查看</a></div>"
            }
        }

    }

    private fun createViewRef(file: File): String {
        return "./database/view?path=${file.absolutePath}"
    }

    @JvmStatic
    fun listDbTables(dbPath: String): String {
        val dbManager = DatabaseManager(dbPath)
        return dbManager.queryAllTableNames().joinToString("") {
            "<option value=\"${it}\">${it}</option>"
        }
    }

    @JvmStatic
    fun listDbTableColumns(dbPath: String, tableName: String): String {
        val dbManager = DatabaseManager(dbPath)
        return dbManager.queryTableColumns(tableName).joinToString("") {
            "<div>${it}</div>"
        }
    }
}