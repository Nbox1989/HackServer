package com.aihuishou.hackserver.core

import android.app.Application
import com.aihuishou.hackathon.IHackServer

object HackServer: IHackServer {
    override fun init(application: Application) = Unit

    override fun startServer() = Unit

    override fun shutdownServer() = Unit

    override fun isRunning(): Boolean = false
}