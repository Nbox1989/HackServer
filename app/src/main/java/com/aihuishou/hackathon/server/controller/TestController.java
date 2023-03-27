package com.aihuishou.hackathon.server.controller;

import com.aihuishou.hackathon.PasteEvent;
import com.aihuishou.hackathon.util.AssetReader;
import com.yanzhenjie.andserver.annotation.*;
import com.yanzhenjie.andserver.util.MediaType;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

@RestController
public class TestController {

    @PostMapping("/user/login")
    public JSONObject login(@RequestBody String str) throws Exception
    {
        JSONObject jsonObject = new JSONObject(str);
        return jsonObject;
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

    @GetMapping(path = "/test", produces = MediaType.TEXT_HTML_VALUE)
    public String getTestPage() {
        return AssetReader.readFromAsset("test.html");
    }


}