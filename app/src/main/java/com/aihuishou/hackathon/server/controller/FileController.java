package com.aihuishou.hackathon.server.controller;

import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RestController;

import java.io.File;

@RestController
public class FileController {

    @GetMapping(path = "/files/create")
    public String createFolder(@QueryParam("folder") String folder) {
        File f = new File(folder);
        boolean result = f.mkdir();
        return result?"创建成功":"创建失败";
    }

}
