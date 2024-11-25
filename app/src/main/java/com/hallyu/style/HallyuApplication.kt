package com.hallyu.style

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.hallyu.style.utilities.PreferencesManager
import dagger.hilt.android.HiltAndroidApp
import io.paperdb.Paper
import java.util.Locale

@HiltAndroidApp
class HallyuApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        PreferencesManager.with(this)
        Paper.init(this)
//        val languageCode = PreferencesManager.get<String>("lang") ?: "en"
        setApplicationLanguage(this)
    }
    fun setApplicationLanguage(context: Context) {
        val languageCode = PreferencesManager.get<String>("lang") ?: "en"
        val activityRes = context.resources
        val activityConf = activityRes.configuration
        val newLocale = Locale(languageCode)
        activityConf.setLocale(newLocale)
        activityConf.setLayoutDirection(newLocale)
        activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)

        val applicationRes = context.resources
        val applicationConf = applicationRes.configuration
        applicationConf.setLocale(newLocale)
        applicationRes.updateConfiguration(
            applicationConf, applicationRes.displayMetrics
        )
    }
    companion object{
        var lang =0
    }
}