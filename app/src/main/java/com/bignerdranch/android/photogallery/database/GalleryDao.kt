package com.bignerdranch.android.photogallery.database

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bignerdranch.android.photogallery.model.Gallery

interface GalleryDao {
    @Query("SELECT * FROM gallery ORDER BY id ASC")
    fun getSearches(): LiveData<List<Gallery>>

    @Insert
    suspend fun addSearch(gallery: Gallery)

    @Delete
    suspend fun removeSearch(gallery: Gallery)

    @Query("DELETE FROM gallery")
    suspend fun clearAll()
}