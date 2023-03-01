package com.kostlin.fragment

import android.content.Context

class SharedPreferencesManager(context: Context) {

    private val name = "DEFAULT"
    private val DAY_KEY = "DAY_KEY"

    private val SAVED_URL_KEY = "URL"

    private val pref = context.getSharedPreferences(name,Context.MODE_PRIVATE)

    fun getDay() : Int {
        return pref.getInt(DAY_KEY,1)
    }

    fun putDay(dayNumber : Int){
        pref.edit().putInt(DAY_KEY,dayNumber).apply()
    }


    fun getURL() : String{
        return pref.getString(SAVED_URL_KEY,"") ?: ""
    }

    fun putURL(url : String){
        pref.edit().putString(SAVED_URL_KEY,url)
    }

}