package com.aihuishou.hackathon

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
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
import androidx.lifecycle.MutableLiveData
import com.aihuishou.hackathon.ui.theme.HackServerTheme
import com.aihuishou.hackathon.util.NotificationUtil
import io.sentry.*
import io.sentry.SentryOptions.BeforeSendCallback
import io.sentry.android.core.SentryAndroid
import io.sentry.android.core.SentryAndroidOptions
import java.lang.reflect.Method

class MainActivity : ComponentActivity() {

    private val isEnabledLiveData = MutableLiveData<Boolean>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sentryDsn = "https://ce4c2b526032420d83391c16267cf752@ahs-sentry.aihuishou.com/49"
//        SentryAndroid.init(context, options -> {
//            options.setDsn(sentryDsn);
//            options.setEnvironment(AppUtils.isAppDebug()?"debug":"release");
//        });

        //        SentryAndroid.init(context, options -> {
//            options.setDsn(sentryDsn);
//            options.setEnvironment(AppUtils.isAppDebug()?"debug":"release");
//        });
        SentryAndroid.init(this) {
            it.setDsn(sentryDsn)
            it.setDebug(true)
            it.cacheDirPath = externalCacheDir?.absolutePath ?: cacheDir.absolutePath // 默认就是cacheDir，即data\data\包名\cache
            it.environment = "test" // 环境标识，如生产环境、测试环境，随便自定义字符串
            it.beforeSend = SentryOptions.BeforeSendCallback { event, hint ->
                // BeforeSendCallback主要就是上传前的拦截器，比如设置debug数据不上报等，具体看需求
                return@BeforeSendCallback if (event.level == SentryLevel.DEBUG) null else event
            }

        }



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
    }

    override fun onResume() {
        super.onResume()
        isEnabledLiveData.value = NotificationUtil.isNotifyEnabled(this)
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
//                val intent = Intent(this, ServerActivity::class.java)
//                startActivity(intent)

                toGrantPermission()
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

