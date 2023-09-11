package com.aihuishou.hackserver.core

import android.app.Application
import android.util.Log
import com.aihuishou.hackathon.IHackServer
import com.aihuishou.hackserver.core.utils.NetUtil
import com.blankj.utilcode.util.ToastUtils
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import java.util.concurrent.TimeUnit

object HackServer: IHackServer {

    @JvmField
    var coreApplication: Application? = null

    private var mServer: Server? = null

    override fun init(application: Application) {
        coreApplication = application
    }

    override fun startServer() {
        if(coreApplication == null) {
            ToastUtils.showShort("未初始化")
        } else if(isRunning()) {
            ToastUtils.showShort("服务已启动")
        } else {
            val ipAddress = NetUtil.getLocalIPAddress().toString()
            mServer = AndServer.webServer(coreApplication!!)
                .port(9999)
                .timeout(10, TimeUnit.SECONDS)
                .listener(object : Server.ServerListener {
                    override fun onStarted() {
                        Log.i("andServer", "onStarted")
                        ToastUtils.showLong("服务启动成功，$ipAddress:9999")
                    }

                    override fun onStopped() {
                        Log.i("andServer", "onStopped")
                        ToastUtils.showLong("服务已关闭")
                    }

                    override fun onException(e: java.lang.Exception?) {
                        Log.i("andServer", "onException")
                        ToastUtils.showLong("服务发生异常 ${e?.message}")
                    }
                })
                .build()
            mServer?.startup()
        }
    }

    override fun shutdownServer() {
        if(isRunning()) {
            mServer?.shutdown()
        }
        mServer = null
    }

    override fun isRunning(): Boolean {
        return mServer?.isRunning == true
    }
}