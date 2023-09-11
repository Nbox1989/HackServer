package com.aihuishou.hackathon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.aihuishou.hackathon.util.NetUtil
import com.aihuishou.hackserver.core.HackServer

class ServerActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initServer()
        setContent {
            ServerContent()
        }
    }

    private fun initServer() {
        HackServer.startServer()
    }


    @Composable
    fun ServerContent() {
        Column{
            val ipAddress = NetUtil.getLocalIPAddress().toString()
            Text(text = ipAddress)
        }
    }
}