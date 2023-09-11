package com.aihuishou.hackathon

import android.app.Application

interface IHackServer {

    fun init(application: Application)

    fun startServer()

    fun shutdownServer()

    fun isRunning(): Boolean
}