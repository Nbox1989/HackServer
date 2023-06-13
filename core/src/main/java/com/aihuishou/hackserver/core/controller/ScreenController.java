package com.aihuishou.hackserver.core.controller;

import com.aihuishou.hackserver.core.func.ScreenFunc;
import com.aihuishou.hackserver.core.utils.AssetReader;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.File;

@RestController
public class ScreenController {

    @GetMapping(path = "/screen/shot", produces = MediaType.TEXT_HTML_VALUE)
    public String getScreenShotPage() {
        return AssetReader.readFromAsset("screen_shot.html");
    }

    @GetMapping(path = "/screen/record", produces = MediaType.TEXT_HTML_VALUE)
    public String getScreenRecordPage() {
        return AssetReader.readFromAsset("screen_record.html");
    }

    @GetMapping(path = "/screen/shot/start")
    public String requestScreenShot() {
        new ScreenFunc().requestScreenShot();
        return "请在手机上点击";
    }


}