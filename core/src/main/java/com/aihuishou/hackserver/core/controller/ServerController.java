package com.aihuishou.hackserver.core.controller;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;

import com.aihuishou.hackserver.core.HackServer;
import com.aihuishou.hackserver.core.func.ActivityFieldsFunc;
import com.aihuishou.hackserver.core.func.FileListFunc;
import com.aihuishou.hackserver.core.func.PasteFunc;
import com.aihuishou.hackserver.core.utils.InputReader;
import com.blankj.utilcode.util.ActivityUtils;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PathVariable;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;
import com.yanzhenjie.andserver.util.MediaType;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

@RestController
public class ServerController {
    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String getDefaultPage() {
    return readFromAsset("home.html");
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
        new PasteFunc().pasteContentToClipBoard(str);
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


    @GetMapping(path = "/test", produces = MediaType.TEXT_HTML_VALUE)
    public String getTestPage() {
        return readFromAsset("test.html");
    }

    @GetMapping(path = "/sniffer")
    public String serverSniffer() {
        return Build.BRAND + " " + Build.MODEL +  " Android " + Build.VERSION.RELEASE;
    }

    private String readFromAsset(String assetFileName) {
        String jsContent = "";
        try {
            if(HackServer.coreApplication == null) {
                jsContent = "";
            } else {
                InputStream inputStream = HackServer.coreApplication.getAssets().open(assetFileName);
                jsContent = InputReader.readFromInputStream(inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsContent;
    }
}