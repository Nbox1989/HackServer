package com.aihuishou.hackathon.server.controller;

import android.app.Activity;

import com.aihuishou.hackathon.application.HackApplication;
import com.aihuishou.hackathon.server.func.ActivityFieldsFunc;
import com.aihuishou.hackathon.server.func.DatabaseFunc;
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
        return AssetReader.readFromAsset("database_home.html");
    }

    @GetMapping(path = "/database/home/folder")
    public String getHomeFolder() {
        File filesDir = HackApplication.instance.getDatabasePath("1").getParentFile();
        if(filesDir == null) {
            return "";
        } else{
            return filesDir.getAbsolutePath();
        }
    }

    @GetMapping(path = "/database/home/list")
    public String getHomeFolderDbFiles() {
        return DatabaseFunc.listDbHomeFiles();
    }

    @GetMapping(path = "/database/view", produces = MediaType.TEXT_HTML_VALUE)
    public String viewDatabase(@QueryParam("path") String path) {
        return AssetReader.readFromAsset("database_view.html");
    }

    @GetMapping(path = "/database/tables")
    public String queryDbTables(@QueryParam("path") String path) {
        return DatabaseFunc.listDbTables(path);
    }

    @GetMapping(path = "/database/columns")
    public String queryDbTables(@QueryParam("path") String path, @QueryParam("table") String table) {
        return DatabaseFunc.listDbTableColumns(path, table);
    }
}