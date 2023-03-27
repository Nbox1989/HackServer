package com.aihuishou.hackathon.server.controller;

import android.app.Activity;

import com.aihuishou.hackathon.server.func.ActivityFieldsFunc;
import com.aihuishou.hackathon.util.AssetReader;
import com.blankj.utilcode.util.ActivityUtils;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;

@RestController
public class ActivityController {
    @GetMapping(path = "/activity", produces = MediaType.TEXT_HTML_VALUE)
    public String getCurPathNodes() {
        Activity activity = ActivityUtils.getTopActivity();
        String html = AssetReader.readFromAsset("activity.html");
        return html.replace("[ACTIVITY_NAME]", activity.getClass().getName());
    }

    @GetMapping(path = "/activity/fields")
    public String getActivityFields(@QueryParam("path") String path) {
        return new ActivityFieldsFunc().getFieldsString(path);
    }
}