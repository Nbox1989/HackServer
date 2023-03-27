package com.aihuishou.hackathon.server.controller;

import android.text.TextUtils;

import com.aihuishou.hackathon.util.AssetReader;
import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;


@RestController
public class ClipboardController {

    @GetMapping(path = "/clipboard", produces = MediaType.TEXT_HTML_VALUE)
    public String getHomePage() {
        return AssetReader.readFromAsset("clipboard.html");
    }


    @PostMapping("/clipboard/copy")
    public String postPaste(@RequestBody String str) throws Exception
    {
        ClipboardUtils.copyText(ClipboardUtils.getLabel(), str);
        ToastUtils.showLong(str + "复制成功");
        return "ok";
    }

    @GetMapping(path = "/clipboard/content")
    public String getClipboardContent() {
        String content = ClipboardUtils.getText().toString();
        if(TextUtils.isEmpty(content)) {
            return "<span style=\"color: red\">【没有内容】</span>";
        } else {
            return "<span style=\"color: blue\">" + content + "</span>";
        }
    }

    @PostMapping(path = "/clipboard/clear")
    public String clearClipboardContent() {
        ClipboardUtils.clear();
        ToastUtils.showLong("剪切板内容已清除");
        return "ok";
    }
}
