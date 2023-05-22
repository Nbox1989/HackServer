package com.aihuishou.hackserver.core.func

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import com.aihuishou.hackserver.core.HackServer
import com.blankj.utilcode.util.ToastUtils

class PasteFunc {

    fun pasteContentToClipBoard(content: String) {
        if(HackServer.coreApplication == null) {
            ToastUtils.showShort("sdk not initialized")
        } else {
            (HackServer.coreApplication!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
                .setPrimaryClip(
                    ClipData(
                        ClipDescription(
                            "content",
                            arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                        ),
                        ClipData.Item(content)
                    )
                )
            ToastUtils.showShort("$content 复制成功")
        }
    }
}