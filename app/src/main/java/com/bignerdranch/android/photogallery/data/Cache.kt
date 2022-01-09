package com.bignerdranch.android.photogallery.data

import android.graphics.Bitmap
import android.util.Log
import androidx.collection.LruCache

class Cache {
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    val cacheSize = maxMemory / 8
    private var memoryCache : LruCache<String, Bitmap>
     = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, value: Bitmap): Int {
            Log.i("Cache", "${value.byteCount}")
            return value.byteCount / 1024
        }
    }

    fun addBitmapToMemoryCache(key: String, value: Bitmap) {
        if (getBitmapFromCache(key) == null) {
            memoryCache.put(key, value)
        }
        Log.i("Cache", "Image($value, key $key) added to cache")
    }

    fun getBitmapFromCache(key: String): Bitmap? {
        Log.i("Cache", "Image(key $key) gotten from cache")
        return memoryCache.get(key)
    }
}

/** Based on a challenge. */