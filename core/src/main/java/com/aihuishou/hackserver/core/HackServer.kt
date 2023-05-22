package com.aihuishou.hackserver.core

import android.app.Application

object HackServer {

    @JvmField
    var coreApplication: Application? = null

    @JvmStatic
    fun init(application: Application) {
        coreApplication = application
    }
}