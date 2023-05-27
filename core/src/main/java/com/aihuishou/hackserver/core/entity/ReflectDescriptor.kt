package com.aihuishou.hackserver.core.entity

data class ReflectDescriptor(
    val className: String?,
    val items: List<ReflectItem>?,
)

data class ReflectItem(
    val type: Int,
    val fieldName: String?,
    val methodName: String?,
    val methodParams: List<MethodParam>?,
) {
    companion object {
        const val TYPE_CONSTRUCTOR = 0
        const val TYPE_FIELD = 1
        const val TYPE_METHOD = 2
    }
}

data class MethodParam(
    val stringValue: String?,
    val className: String?,
)
