package com.kostlin.fragment

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.onesignal.OSNotificationOpenedResult
import com.onesignal.OneSignal

const val ONESIGNAL_APP_ID = "here is ID"

class OneSignal() : Application() {
    override fun onCreate() {
        super.onCreate()

        // Инициализация OneSignal
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)

        // Обработка приходящих уведомлений
        OneSignal.setNotificationOpenedHandler(NotificationOpenedHandler(this))
    }

    private inner class NotificationOpenedHandler(val context: Context) :
        OneSignal.OSNotificationOpenedHandler {

        override fun notificationOpened(result: OSNotificationOpenedResult) {
            val url = result.notification.launchURL ?: return
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)

        }
    }
}