package com.kostlin.fragment


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kostlin.fragment.R.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    val defaults = mapOf(
        "FireBaseLink" to "https://conversionleadstraffic.info/dv5RmK"
    )
    val basic = defaults["FireBaseLink"].toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)
        supportActionBar?.hide()

//        val activity = MainFragment()
//        val bundle = Bundle()
//        bundle.putString("FireBaseLink", basic)
//        activity.arguments = bundle
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, activity)
//            .commit()
        val intent = Intent(this, SpashScreenActivity::class.java)
        intent.putExtra("FireBaseLink", basic)
        startActivity(intent)

    }


}