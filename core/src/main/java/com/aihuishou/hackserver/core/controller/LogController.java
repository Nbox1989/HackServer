package com.aihuishou.hackserver.core.controller;

import com.aihuishou.hackserver.core.func.LogFunc;
import com.aihuishou.hackserver.core.utils.AssetReader;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;


@RestController
public class LogController {

    @GetMapping(path = "/log", produces = MediaType.TEXT_HTML_VALUE)
    public String getLogHomePage() {
        return AssetReader.readFromAsset("log.html");
    }

    @GetMapping(path = "/log/status")
    public String logcatStatus() {
        return new LogFunc().queryLogProcessStatus();
    }

    @GetMapping(path = "/log/on")
    public String logcatOn() {
        return new LogFunc().executeLogcat();
    }

    @GetMapping(path = "/log/off", produces = MediaType.TEXT_HTML_VALUE)
    public String logcatOff() {
        return new LogFunc().shutdownLogcat();
    }

}
