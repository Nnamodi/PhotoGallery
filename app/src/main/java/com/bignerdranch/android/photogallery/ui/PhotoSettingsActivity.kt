package com.bignerdranch.android.photogallery.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.photogallery.R

class PhotoSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery_settings)
        val toolbar = findViewById<Toolbar>(R.id.settings_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val fm = supportFragmentManager
        val currentFragment = fm.findFragmentById(R.id.fragment_settings_container)
        if (currentFragment == null) {
            val fragment = PhotoSettingsFragment.newInstance()
            fm.beginTransaction()
                .add(R.id.fragment_settings_container, fragment)
                .commit()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    companion object  {
        fun newIntent(context: Context): Intent {
            return Intent(context, PhotoSettingsActivity::class.java)
        }
    }
}