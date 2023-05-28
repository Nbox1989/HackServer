package com.aihuishou.hackserver.core.controller;

import com.aihuishou.hackserver.core.utils.AssetReader;
import com.aihuishou.hackserver.core.vpn.VpnFunc;
import com.yanzhenjie.andserver.annotation.GetMapping;
import com.yanzhenjie.andserver.annotation.RestController;
import com.yanzhenjie.andserver.util.MediaType;


@RestController
public class VpnController {

    @GetMapping(path = "/vpn", produces = MediaType.TEXT_HTML_VALUE)
    public String getVpnHomePage() {
        return AssetReader.readFromAsset("vpn.html");
    }

    @GetMapping(path = "/vpn/status")
    public Integer getVpnStatus() {
        return VpnFunc.queryVpnStatus();
    }

    @GetMapping(path = "/vpn/on")
    public Integer startupVpn() {
        return VpnFunc.startup();
    }

    @GetMapping(path = "/vpn/off")
    public Integer shutdownVpn() {
        return VpnFunc.shutdown();
    }

    @GetMapping(path = "/vpn/file")
    public String getVpnPackageFilePath() {
        return VpnFunc.getVpnPackageFilePath();
    }
}
