package com.aihuishou.hackserver.core.controller;

import com.aihuishou.hackserver.core.utils.AssetReader;
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