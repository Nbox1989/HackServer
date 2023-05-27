package com.aihuishou.hackserver.core.controller;

import android.os.Environment;

import com.aihuishou.hackserver.core.HackServer;
import com.aihuishou.hackserver.core.func.FileListFunc;
import com.aihuishou.hackserver.core.utils.AssetReader;
import com.aihuishou.hackserver.core.utils.InputReader;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.PostMapping;
import com.yanzhenjie.andserver.annotation.QueryParam;
import com.yanzhenjie.andserver.annotation.RequestParam;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.framework.body.FileBody;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.ResponseBody;
import com.yanzhenjie.andserver.http.multipart.MultipartFile;
import com.yanzhenjie.andserver.util.MediaType;

import java.io.File;

@RestController
public class FileController {

    @GetMapping(path = "/files/", produces = MediaType.TEXT_HTML_VALUE)
    public String getEnumFileDirs() {
        return AssetReader.readFromAsset("files_home.html");
    }

    @GetMapping(path = "/files/internal", produces = MediaType.TEXT_HTML_VALUE)
    public String getInternalFiles() {
        if(HackServer.coreApplication == null) {
            return "";
        }
        File filesDir = HackServer.coreApplication.getFilesDir().getParentFile();
        String html = AssetReader.readFromAsset("files.html");
        return html.replace("[ROOT_FILE_PATH]", filesDir.getAbsolutePath());
    }

    @GetMapping(path = "/files/external", produces = MediaType.TEXT_HTML_VALUE)
    public String getExternalFiles() {
        if(HackServer.coreApplication == null) {
            return "";
        }
        File filesDir = HackServer.coreApplication.getExternalCacheDir().getParentFile();
        String html = AssetReader.readFromAsset("files.html");
        return html.replace("[ROOT_FILE_PATH]", filesDir.getAbsolutePath());
    }

    @GetMapping(path = "/files/sdcard", produces = MediaType.TEXT_HTML_VALUE)
    public String getSdcardFiles() {
        File filesDir = Environment.getExternalStorageDirectory();
        String html = AssetReader.readFromAsset("files.html");
        return html.replace("[ROOT_FILE_PATH]", filesDir.getAbsolutePath());
    }

    @GetMapping(path = "/files/root", produces = MediaType.TEXT_HTML_VALUE)
    public String getRootFiles() {
        File filesDir = new File("/");
        String html = AssetReader.readFromAsset("files.html");
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

    @GetMapping(path = "/files/view", produces = MediaType.TEXT_HTML_VALUE)
    public String viewFile(HttpResponse response, @QueryParam(name = "filePath") String filePath){
        File file = new File(filePath);
        return "<html><body><pre>" + InputReader.readFormFile(file) + "</pre></body></html>";


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

    @GetMapping(path = "/files/create")
    public String createFolder(@QueryParam("folder") String folder) {
        File f = new File(folder);
        boolean result = f.mkdir();
        return result?"创建成功":"创建失败";
    }

}
