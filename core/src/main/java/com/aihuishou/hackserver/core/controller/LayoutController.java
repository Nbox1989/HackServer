package com.aihuishou.hackserver.core.controller;

import com.aihuishou.hackserver.core.utils.AssetReader;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;


@RestController
public class LayoutController {

    @GetMapping(path = "/layout", produces = MediaType.TEXT_HTML_VALUE)
    public String getLayoutHomePage() {
        return AssetReader.readFromAsset("layout.html");
    }

}
