package com.aihuishou.hackserver.core.func

import com.aihuishou.hackserver.core.entity.MethodParam
import com.aihuishou.hackserver.core.entity.ReflectDescriptor
import com.aihuishou.hackserver.core.entity.ReflectItem
import com.google.gson.Gson
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties
import kotlin.reflect.jvm.isAccessible

class ReflectFunc {

    fun tryReflect(descriptorStr: String): String {
        try {
            val descriptor = Gson().fromJson(descriptorStr, ReflectDescriptor::class.java)
            if(descriptor.className.isNullOrEmpty() || descriptor.items.isNullOrEmpty()) {
                return "不是一个合法的数据！"
            }
            var any = dealFirstReflectItem(descriptor.className, descriptor.items[0])
            descriptor.items.forEachIndexed { index, reflectItem ->
                if(index > 0) {
                    if(any == null) {
                        return "在第${index}层出错了！"
                    } else {
                        any = dealReflectItem(any!!, reflectItem)
                    }
                }
            }
            return any.toString()
        } catch (e: Exception) {
            return e.message.toString()
        }
    }

    private fun dealFirstReflectItem(className: String, item: ReflectItem): Any? {
        val ktClass = Class.forName(className).kotlin
        return when(item.type) {
            ReflectItem.TYPE_CONSTRUCTOR -> {
                val constructor = ktClass.constructors.filter {
                    it.parameters.size == (item.methodParams?.size?:0)
                }
                constructor.forEach {
                    try {
                        it.isAccessible = true
                        val params = item.methodParams?.map { methodParam ->
                            methodParam.toObject()
                        }?: emptyList()
                        val ret = it.call(*params.toTypedArray())
                        return ret
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            ReflectItem.TYPE_FIELD -> {
                val field = ktClass.staticProperties.firstOrNull { it.name == item.fieldName }
                val ret = field?.let {
                    it.isAccessible = true
                    it.call()
                }
                ret
            }
            ReflectItem.TYPE_METHOD -> {
                val method = ktClass.staticFunctions.filter {
                    it.name == item.methodName
                }
                method.forEach {
                    try {
                        it.isAccessible = true
                        val ret = it.call()
                        return ret
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else -> {
                null
            }
        }
    }

    private fun dealReflectItem(any: Any, item: ReflectItem): Any? {
        return when(item.type) {
            ReflectItem.TYPE_FIELD -> {
                val field = any::class.memberProperties.firstOrNull { it.name == item.fieldName }
                val ret = field?.let {
                    it.isAccessible = true
                    it.call(any)
                }
                ret
            }
            ReflectItem.TYPE_METHOD -> {
                val methods = any::class.memberFunctions.filter { it.name == item.methodName }
                methods.forEach {
                    try {
                        val params = item.methodParams?.map { methodParam ->
                            methodParam.toObject()
                        }?: emptyList()
                        it.isAccessible = true
                        val ret = it.call(any, *params.toTypedArray())
                        return ret
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            else -> {
                null
            }
        }
    }
}

private fun MethodParam.toObject(): Any? {
    if(this.stringValue == null || this.className == null) {
        return  null
    }
    try {
        if (!Class.forName(this.stringValue).isPrimitive) {
            return Gson().fromJson(
                this.stringValue,
                Class.forName(this.stringValue)
            )
        }
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    return when  {
        this.className.endsWith("Int", false) -> this.stringValue.toInt()
        this.className.endsWith("Long", false) -> this.stringValue.toLong()
        this.className.endsWith("Double", false) -> this.stringValue.toDouble()
        this.className.endsWith("Float", false) -> this.stringValue.toFloat()
        this.className.endsWith("Boolean", false) -> this.stringValue.toBoolean()
        this.className.endsWith("String", false) -> this.stringValue.toBoolean()
        else -> null
    }
}
