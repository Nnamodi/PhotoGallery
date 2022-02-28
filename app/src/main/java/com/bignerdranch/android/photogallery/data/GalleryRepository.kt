package com.bignerdranch.android.photogallery.data

import androidx.lifecycle.LiveData
import com.bignerdranch.android.photogallery.database.GalleryDao
import com.bignerdranch.android.photogallery.model.Gallery

class GalleryRepository(private val galleryDao: GalleryDao) {
    val getSearches: LiveData<List<Gallery>> = galleryDao.getSearches()

    suspend fun addSearch(gallery: Gallery) {
        galleryDao.addSearch(gallery)
    }

    suspend fun removeSearch(gallery: Gallery) {
        galleryDao.removeSearch(gallery)
    }

    suspend fun clearAll() {
        galleryDao.clearAll()
    }
}