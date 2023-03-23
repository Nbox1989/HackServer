package com.aihuishou.hackathon

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import com.aihuishou.hackathon.util.Constants
import com.aihuishou.hackathon.util.NetUtil
import com.yanzhenjie.andserver.AndServer
import com.yanzhenjie.andserver.Server
import io.sentry.Sentry
import io.sentry.SentryLevel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.concurrent.TimeUnit

class ServerActivity: ComponentActivity() {

    private var mServer: Server? = null

    private val serverStarted = MutableLiveData(false)
    private val serverErrorMsg = MutableLiveData<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            throw Exception("This is a test.")
        } catch (e: Exception) {
            Sentry.captureMessage("test again", SentryLevel.FATAL)
            Sentry.captureException(e)
        }

        readAssets()
        initServer()
        setContent {
            ServerContent()
        }
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mServer?.shutdown()
        EventBus.getDefault().unregister(this)
    }

    private fun initServer() {
        mServer = AndServer.webServer(this)
            .port(9999)
            .timeout(10, TimeUnit.SECONDS)
            .listener(object : Server.ServerListener {
                override fun onStarted() {
                    Log.i("andServer", "onStarted")
                    serverStarted.value = true
                }

                override fun onStopped() {
                    Log.i("andServer", "onStopped")
                    serverStarted.value = false
                }

                override fun onException(e: java.lang.Exception?) {
                    Log.i("andServer", "onException")
                    serverStarted.value = false
                    serverErrorMsg.value = e?.message?:"unknown error"
                }
            })
            .build()
        mServer?.startup()
    }

    private fun readAssets() {
        var jsContent = ""
        try {
            val inputStream = assets.open("home.html")
            inputStream.bufferedReader().useLines { lines ->
                jsContent = lines.joinToString(separator="")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Constants.HOME_PAGE_HTML = jsContent
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onReceivePasteEvent(event: PasteEvent) {
        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
            .setPrimaryClip(
                ClipData(
                    ClipDescription(
                        "content",
                        arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    ),
                    ClipData.Item(event.content)
                )
            )
        Toast.makeText(this, "${event.content} 复制成功", Toast.LENGTH_SHORT).show()
    }

    @Composable
    fun ServerContent() {
        Column{
            val ipAddress = NetUtil.getLocalIPAddress().toString()
            Text(text = ipAddress)
            val serverStarted by serverStarted.observeAsState()
            val serverErrorMsg by serverErrorMsg.observeAsState()
            Text(text = "服务器启动${if(serverStarted == true)"成功" else "失败"}")
            Text(text = "错误内容:$serverErrorMsg")
        }
    }
}