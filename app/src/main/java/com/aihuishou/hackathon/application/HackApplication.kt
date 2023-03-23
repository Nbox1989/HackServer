package com.aihuishou.hackathon.application

import android.app.Application

class HackApplication: Application() {

    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}