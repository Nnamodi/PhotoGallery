package com.bignerdranch.android.photogallery.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.content.ContextCompat
import com.bignerdranch.android.photogallery.R
import com.bignerdranch.android.photogallery.data.QueryPreferences
import com.bignerdranch.android.photogallery.util.VisibleFragment

class PhotoSettingsFragment : VisibleFragment() {
    private lateinit var radioGroup: RadioGroup
    private lateinit var darkModeSwitch: Switch
    private lateinit var roland: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_gallery_settings, container, false)
        darkModeSwitch = view.findViewById(R.id.dark_mode_switch)
        darkModeSwitch.isChecked = QueryPreferences.getSwitchState(requireContext())
        darkModeSwitch.setOnCheckedChangeListener { switch, isChecked ->
            if (switch.isChecked) {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }
            QueryPreferences.setSwitchedState(requireContext(), isChecked)
        }
        radioGroup = view.findViewById(R.id.radio_group)
        radioGroup.check(QueryPreferences.getRadioButtonId(requireContext()))
        radioGroup.setOnCheckedChangeListener { _: RadioGroup, checkedButtonId: Int ->
            when (checkedButtonId) {
                R.id.browser_app_button -> {
                    QueryPreferences.setBrowserChoice(requireContext(), "Browser app")
                    Log.i("RadioButton", "Browser app")
                }

                R.id.web_view_button -> {
                    QueryPreferences.setBrowserChoice(requireContext(), "WebView")
                    Log.i("RadioButton", "WebView")
                }

                R.id.custom_view_button -> {
                    QueryPreferences.setBrowserChoice(requireContext(), "Custom View")
                    Log.i("RadioButton", "Custom View")
                }
            }
            QueryPreferences.setRadioButtonId(requireContext(), checkedButtonId)
        }
        roland = view.findViewById(R.id.roland)
        val switchState = QueryPreferences.getSwitchState(requireContext())
        if (switchState) {
            roland.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        }
        return view
    }

    companion object {
        fun newInstance() = PhotoSettingsFragment()
    }
}