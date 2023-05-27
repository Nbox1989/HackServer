package com.aihuishou.hackserver.core.controller;

import com.aihuishou.hackserver.core.func.ReflectFunc;
import com.aihuishou.hackserver.core.utils.AssetReader;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.RequestBody;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;


@RestController
public class ReflectController {

    @GetMapping(path = "/reflect/", produces = MediaType.TEXT_HTML_VALUE)
    public String getHomePage() throws Exception
    {
        return AssetReader.readFromAsset("reflect.html");
    }


    @PostMapping("/reflect/request")
    public String requestReflect(@RequestBody String request) throws Exception
    {
        return new ReflectFunc().tryReflect(request);
    }
}