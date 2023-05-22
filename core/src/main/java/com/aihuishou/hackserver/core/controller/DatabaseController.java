package com.aihuishou.hackserver.core.controller;

import com.aihuishou.hackserver.core.HackServer;
import com.aihuishou.hackserver.core.func.DatabaseFunc;
import com.aihuishou.hackserver.core.utils.AssetReader;
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
        if(HackServer.coreApplication == null) {
            return "";
        }
        File filesDir = HackServer.coreApplication.getDatabasePath("1").getParentFile();
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

    @GetMapping(path = "/database/records")
    public String queryDbRecords(@QueryParam("path") String path, @QueryParam("table") String table) {
        return DatabaseFunc.listDbTableRecords(path, table);
    }

    @GetMapping(path = "/database/recordCount")
    public String queryDbTableRecordCount(@QueryParam("path") String path, @QueryParam("table") String table) {
        return DatabaseFunc.queryDbTableRecordCount(path, table);
    }
}