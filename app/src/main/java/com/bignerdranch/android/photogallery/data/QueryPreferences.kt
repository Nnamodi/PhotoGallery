package com.bignerdranch.android.photogallery.data

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.core.content.edit

private const val PREF_SEARCH_QUERY = "searchQuery"
private const val PREF_LAST_RESULT_ID = "lastResultId"
private const val PREF_IS_POLLING = "isPolling"
private const val BROWSER_SETTING= "browserSetting"
private const val RADIO_BUTTON_ID = "radioButtonId"
private const val SWITCHED = "switchedOn"
private const val SPLASH = "paused"

object QueryPreferences {
    fun getStoredQuery(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_SEARCH_QUERY, "")!!
    }

    fun setStoredQuery(context: Context, query: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit {
                putString(PREF_SEARCH_QUERY, query)
            }
    }

    fun getLastResultId(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(PREF_LAST_RESULT_ID, "")!!
    }

    fun setLastResultId(context: Context, lastResultId: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(PREF_LAST_RESULT_ID, lastResultId)
        }
    }

    fun isPolling(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_IS_POLLING, false)
    }

    fun setPolling(context: Context, isOn: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(PREF_IS_POLLING, isOn)
        }
    }

    fun getBrowserChoice(context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(BROWSER_SETTING, "")!!
    }

    fun setBrowserChoice(context: Context, browser: String) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putString(BROWSER_SETTING, browser)
        }
    }

    fun getRadioButtonId(context: Context): Int {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(RADIO_BUTTON_ID, 0)
    }

    fun setRadioButtonId(context: Context, id: Int) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putInt(RADIO_BUTTON_ID, id)
        }
    }

    fun getSwitchState(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(SWITCHED, false)
    }

    fun setSwitchedState(context: Context, switchedOn: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(SWITCHED, switchedOn)
        }
    }

    fun getSplashPaused(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(SPLASH, false)
    }

    fun setSplashedPaused(context: Context, isPaused: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit {
            putBoolean(SPLASH, isPaused)
        }
    }
}