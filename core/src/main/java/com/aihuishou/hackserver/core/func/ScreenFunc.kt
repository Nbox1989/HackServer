package com.aihuishou.hackserver.core.func

import android.content.Intent
import com.aihuishou.hackserver.core.screen.shot.ScreenShotActivity
import com.blankj.utilcode.util.ActivityUtils


class ScreenFunc {


    fun requestScreenShot() {
        ActivityUtils.getTopActivity().startActivity(
            Intent(ActivityUtils.getTopActivity(), ScreenShotActivity::class.java)
        )
    }


}