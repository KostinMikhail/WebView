package com.kostlin.fragment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kostlin.fragment.R.*
import com.kostlin.fragment.ui.main.MainFragment
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

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment())
            .commit()
    }

}