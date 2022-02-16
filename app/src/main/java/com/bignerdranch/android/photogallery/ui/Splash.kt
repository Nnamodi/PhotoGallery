package com.bignerdranch.android.photogallery.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bignerdranch.android.photogallery.data.QueryPreferences

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val switchState = QueryPreferences.getSwitchState(this)
        if (switchState) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        splash()
        Log.i("Splashed", "Created")
    }

    private fun splash() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, PhotoGalleryActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}