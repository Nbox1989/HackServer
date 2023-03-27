package com.aihuishou.hackathon.server.controller;

import android.app.Activity;

import com.aihuishou.hackathon.application.HackApplication;
import com.aihuishou.hackathon.server.func.ActivityFieldsFunc;
import com.aihuishou.hackathon.util.AssetReader;
import com.blankj.utilcode.util.ActivityUtils;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.File;

@RestController
public class DatabaseController {
    @GetMapping(path = "/database", produces = MediaType.TEXT_HTML_VALUE)
    public String getCurPathNodes() {
        return AssetReader.readFromAsset("database.html");
    }

    @GetMapping(path = "/database/home/folder")
    public String getHomeFolder() {
        File filesDir = HackApplication.instance.getDatabasePath("/");
        return filesDir.getAbsolutePath();
    }

    @GetMapping(path = "/database/home/list")
    public String getHomeFolderDbFiles(@QueryParam("path") String path) {
        return "";
    }
}