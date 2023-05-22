package com.aihuishou.hackathon.application

import android.app.Application
import com.aihuishou.hackserver.core.HackServer

class HackApplication: Application() {

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        HackServer.init(this)
    }
}