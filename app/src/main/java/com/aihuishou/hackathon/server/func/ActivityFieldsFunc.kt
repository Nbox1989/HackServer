package com.aihuishou.hackathon.server.func

import com.blankj.utilcode.util.ActivityUtils
import com.blankj.utilcode.util.ReflectUtils
import java.lang.reflect.Modifier

class ActivityFieldsFunc {

    fun getFieldsString(path: String): String{
        val separator = "/"

        val activity = ActivityUtils.getTopActivity()
        val pathArray = path.split(separator).filterNot { it.isEmpty() }

        var any: Any? = activity
        for (pathSegment in pathArray) {
            any = ReflectUtils.reflect(any).field(pathSegment).get()
        }

        val sb = StringBuilder()
        if(any != null) {
            val fields = any.javaClass.fields
            for (field in fields) {
                try {
                    val fieldName = field.name
                    if (!Modifier.isStatic(field.modifiers)) {
                        val className = field[any].javaClass.simpleName
                        val value = field.get(any)?.toString()
                        val text = "$fieldName :$className($value)"
                        val id: String = pathArray.joinToString(separator) + separator + fieldName
                        sb.append("<div style = \"margin-left: 20px\" " +
                                "onclick = \"queryFields(event, '$id')\">")
                            .append("👉🏻 $text")
                            .append("<div id = \"$id\"> </div>")
                            .append("</div>")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return sb.toString()
    }

}