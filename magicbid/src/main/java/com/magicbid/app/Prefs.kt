package com.magicbid.app

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Prefs {


    fun setResponseAll(applicationContext: Context, data: List<Adscode>?) {
        val sharePref = applicationContext.getSharedPreferences("UserType", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharePref.edit()

        // Serialize the data using Gson
        val gson = Gson()
        val dataString = gson.toJson(data)

        editor.putString("data_list", dataString)
        editor.apply()
    }
    fun getResponseAll(applicationContext: Context): List<Adscode>? {
        val sharePref = applicationContext.getSharedPreferences("UserType", AppCompatActivity.MODE_PRIVATE)
        val dataString = sharePref.getString("data_list", null)

        if (dataString != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Adscode>>() {}.type
            val dataList: List<Adscode> = gson.fromJson(dataString, type)
            return dataList
        }

        return null
    }

    fun clearAll(context: Context) {
        val sharedPref = context.getSharedPreferences("UserType", AppCompatActivity.MODE_PRIVATE)
        sharedPref.edit().clear().apply()




    }




}
