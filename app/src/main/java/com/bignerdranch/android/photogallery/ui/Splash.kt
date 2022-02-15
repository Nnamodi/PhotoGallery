package com.bignerdranch.android.photogallery.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.photogallery.data.QueryPreferences

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isPaused = QueryPreferences.getSplashPaused(this)
        Log.i("Splashed", "Created, $isPaused")
        if (!isPaused) {
            splash()
        }
    }

    override fun onPause() {
        super.onPause()
        val isPaused = QueryPreferences.getSplashPaused(this)
        QueryPreferences.setSplashedPaused(this, true)
        Log.i("Splashed", "Paused, $isPaused")
    }

    private fun splash() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, PhotoGalleryActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000)
    }
}