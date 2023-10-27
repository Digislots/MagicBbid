package com.magicbid.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Prefs {


    fun setResponseAll(applicationContext: Context, data: List<Adscode>?) {
        val sharePref =
            applicationContext.getSharedPreferences("UserType", AppCompatActivity.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharePref.edit()

        val gson = Gson()
        val dataString = gson.toJson(data)

        editor.putString("data_list", dataString)
        editor.apply()
    }

    fun getResponseAll(applicationContext: Context): List<Adscode>? {
        val sharePref =
            applicationContext.getSharedPreferences("UserType", AppCompatActivity.MODE_PRIVATE)
        val dataString = sharePref.getString("data_list", null)


        if (dataString != null) {
            val gson = Gson()
            val type = object : TypeToken<List<Adscode>>() {}.type

            return try {
                gson.fromJson(dataString, type)
            } catch (e: Exception) {
                // Handle any exceptions that may occur during deserialization
                e.printStackTrace()
                // You can return an empty list or handle the error as needed
                emptyList()
            }
        } else {
            // Handle the case when dataString is null
            // You can return an empty list or handle it differently based on your requirements
            return emptyList()
        }

    }





    fun getAppId(applicationContext: Context?): Int {
        val sharePref = applicationContext?.getSharedPreferences("appCode", AppCompatActivity.MODE_PRIVATE)
         return sharePref?.getInt("appCode", 0) ?: 0
    }
    fun setAppId(applicationContext: Context?, appId: Int) {
        applicationContext?.let { context ->
            try {
                val sharePref = context.getSharedPreferences("appCode", AppCompatActivity.MODE_PRIVATE)
                val editor = sharePref.edit()
                editor.putInt("appCode", appId)
                editor.apply()
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle the exception as needed (e.g., log the error or show a message to the user).
            }
        }
    }



//    fun setAppId(applicationContext: Context?, appId: Int) {
//        val sharePref = applicationContext?.getSharedPreferences("appCode", AppCompatActivity.MODE_PRIVATE)
//        val editor: SharedPreferences.Editor = sharePref!!.edit()
//        editor.putInt("appCode", appId)
//        editor.apply()
//    }


}
