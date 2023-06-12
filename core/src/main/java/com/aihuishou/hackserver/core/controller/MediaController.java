package com.aihuishou.hackserver.core.controller;

import com.aihuishou.hackserver.core.utils.AssetReader;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.File;

@RestController
public class MediaController {

    @GetMapping(path = "/media/home/image", produces = MediaType.TEXT_HTML_VALUE)
    public String getMediaImagePage() {
        return AssetReader.readFromAsset("view_image.html");
    }

    @GetMapping(path = "/media/home/video", produces = MediaType.TEXT_HTML_VALUE)
    public String getMediaVideoPage() {
        return AssetReader.readFromAsset("view_video.html");
    }


    @GetMapping("/media/image")
    public ResponseBody viewImage(HttpResponse response, @QueryParam(name = "filePath") String filePath) throws Exception
    {
        File file= new File(filePath);
        FileBody body= new FileBody(file);
        response.setHeader("Content-Disposition", "attachment;fileName="+file.getName());
        return body;
    }


    @GetMapping("/media/video")
    public ResponseBody viewVideo(HttpResponse response, @QueryParam(name = "filePath") String filePath) throws Exception
    {
        File file= new File(filePath);
        FileBody body= new FileBody(file);
        response.setHeader("Content-Disposition", "attachment;fileName="+file.getName());
        return body;
    }


}