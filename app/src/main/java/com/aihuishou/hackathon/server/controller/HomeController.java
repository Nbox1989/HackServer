package com.aihuishou.hackathon.server.controller;

import com.aihuishou.hackathon.util.AssetReader;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;

@RestController
public class HomeController {
    @GetMapping(path = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String getDefaultPage() {
        return AssetReader.readFromAsset("home.html");
    }

    @GetMapping(path = "/home", produces = MediaType.TEXT_HTML_VALUE)
    public String getHomePage() {
        return AssetReader.readFromAsset("home.html");
    }
}