package com.kostlin.fragment

import android.app.Application
import com.onesignal.OneSignal



const val ONESIGNAL_APP_ID = "here is ID"

class OneSignal() : Application() {
    override fun onCreate() {
        super.onCreate()

        OneSignal.initWithContext(this)

        OneSignal.setAppId("ONESIGNAL_APP_ID")

        OneSignal.promptForPushNotifications()
    }


    class MainApplication : Application() {
        override fun onCreate() {
            super.onCreate()

            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)


            OneSignal.initWithContext(this)
            OneSignal.setAppId(ONESIGNAL_APP_ID)
        }
    }

}