package com.aihuishou.hackathon.server.controller;

import android.app.Activity;
import android.os.Environment;

import com.aihuishou.hackathon.PasteEvent;
import com.aihuishou.hackathon.application.HackApplication;
import com.aihuishou.hackathon.util.InputReader;
import com.blankj.utilcode.util.ActivityUtils;
import com.yanzhenjie.andserver.annotation.*;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;
import com.yanzhenjie.andserver.util.MediaType;
import com.yanzhenjie.andserver.http.ResponseBody;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

@RestController
public class ServerController {
    @GetMapping("/")
    public String ping()
    {
        return "SERVER OK";
    }

    @PostMapping("/user/login")
    public JSONObject login(@RequestBody String str) throws Exception
    {
        JSONObject jsonObject = new JSONObject(str);
        return jsonObject;
    }

    @PostMapping("/paste")
    public String postPaste(@RequestBody String str) throws Exception
    {
        EventBus.getDefault().post(new PasteEvent(str));
        return "ok";
    }

    @GetMapping("/user/item")
    public JSONObject requestItem(@RequestParam("name") String name,
    @RequestParam("id") String id) throws Exception
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", name);
        jsonObject.put("id", id);

        return jsonObject;
    }

    @GetMapping("/user/{userId}")
    public JSONObject getUser(@PathVariable("userId") String userId,
    @QueryParam("key") String key) throws Exception
    {
        JSONObject user = new JSONObject();
        user.put("id", userId);
        user.put("key", key);
        user.put("year", 2022);

        return user;
    }

    @GetMapping(path = "/home", produces = MediaType.TEXT_HTML_VALUE)
    public String getHomePage() {
        return readFromAsset("home.html");
    }

    @GetMapping(path = "/activity/current", produces = MediaType.TEXT_HTML_VALUE)
    public String getCurPathNodes() {
        Activity activity = ActivityUtils.getTopActivity();
        String html = readFromAsset("activity.html");
        return html.replace("[ACTIVITY_NAME]", activity.getClass().getName());
    }

    @GetMapping(path = "/activity/fields")
    public String getActivityFields(@QueryParam("path") String path) {
        return new ActivityFieldsFunc().getFieldsString(path);
    }

    @GetMapping(path = "/test", produces = MediaType.TEXT_HTML_VALUE)
    public String getTestPage() {
        return readFromAsset("test.html");
    }

    private String readFromAsset(String assetFileName) {
        String jsContent = "";
        try {
            InputStream inputStream = HackApplication.instance.getAssets().open(assetFileName);
            jsContent = InputReader.readFromInputStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsContent;
    }

    @GetMapping(path = "/files", produces = MediaType.TEXT_HTML_VALUE)
    public String getEnumFileDirs() {
        return readFromAsset("enum_file_dir.html");
    }

    @GetMapping(path = "/files/internal", produces = MediaType.TEXT_HTML_VALUE)
    public String getInternalFiles() {
        File filesDir = HackApplication.instance.getDataDir();
        String html = readFromAsset("files.html");
        return html.replace("[ROOT_FILE_PATH]", filesDir.getAbsolutePath());
    }

    @GetMapping(path = "/files/external", produces = MediaType.TEXT_HTML_VALUE)
    public String getExternalFiles() {
        File filesDir = HackApplication.instance.getExternalCacheDir().getParentFile();
        String html = readFromAsset("files.html");
        return html.replace("[ROOT_FILE_PATH]", filesDir.getAbsolutePath());
    }

    @GetMapping(path = "/files/sdcard", produces = MediaType.TEXT_HTML_VALUE)
    public String getSdcardFiles() {
        File filesDir = Environment.getExternalStorageDirectory();
        String html = readFromAsset("files.html");
        return html.replace("[ROOT_FILE_PATH]", filesDir.getAbsolutePath());
    }

    @GetMapping(path = "/files/list")
    public String listFiles(@QueryParam("root") String root, @QueryParam(value = "path", required = false) String path) {
        return new FileListFunc().getFilesString(root, path);
    }

    @GetMapping(path = "/files/download")
    public ResponseBody downloadFile(HttpResponse response, @QueryParam(name = "filePath") String filePath){
        File file= new File(filePath);
        FileBody body= new FileBody(file);
        response.setHeader("Content-Disposition", "attachment;fileName="+file.getName());
        return body;
    }

    @PostMapping(path = "/files/upload")
    public String uploadFile(HttpRequest request, HttpResponse response, @RequestParam(name = "file") MultipartFile file, @RequestParam(name = "toFolder") String folder) {
        try {
            File uploadFile = new File(folder);//FileUtils.createUploadFile(file);
            file.transferTo(uploadFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "文件上传成功！";
    }
}