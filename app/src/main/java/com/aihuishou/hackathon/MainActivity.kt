package com.aihuishou.hackathon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.aihuishou.hackathon.ui.theme.HackServerTheme
import com.aihuishou.hackathon.util.NotificationUtil
import java.util.*

class MainActivity : ComponentActivity() {

    private val isEnabledLiveData = MutableLiveData<Boolean>()

    private var resumeFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            HackServerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
        requestPermission()

        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    Log.i("nbox", System.currentTimeMillis().toString())
                }
            },
            1000,
            1000
        )

    }

    override fun onResume() {
        super.onResume()
        isEnabledLiveData.value = NotificationUtil.isNotifyEnabled(this)
        if(!resumeFlag) {
            val intent = Intent(this, ScreenRecordActivity::class.java)
            startActivity(intent)
            resumeFlag = true
        }
    }

    private fun requestPermission() {
        val permissions = Array(1) {
            when(it) {
                0 -> Manifest.permission.WRITE_EXTERNAL_STORAGE
                else -> ""
            }
        }

        if (ContextCompat.checkSelfPermission(this, permissions[0])
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private fun toGrantPermission() {
        val localIntent = Intent()
        //直接跳转到应用通知设置的代码：
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0及以上
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0以上到8.0以下
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", getPackageName());
            localIntent.putExtra("app_uid", getApplicationInfo().uid);
        } else if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {//4.4
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + getPackageName()));
        } else {
            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
            }
        }
        startActivity(localIntent);
    }

    @Composable
    fun Greeting(name: String) {
        val value = isEnabledLiveData.observeAsState()
        Text(text = "Hello $name!, ${value.value}",
            modifier = Modifier.clickable{
                val intent = Intent(this, ServerActivity::class.java)
                startActivity(intent)

//                toGrantPermission()
            })
    }

    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        HackServerTheme {
            Greeting("Android")
        }
    }
}

