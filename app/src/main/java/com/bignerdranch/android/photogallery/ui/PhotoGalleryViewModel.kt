package com.bignerdranch.android.photogallery.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bignerdranch.android.photogallery.api.FlickrFetchr
import com.bignerdranch.android.photogallery.model.GalleryItem

class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData: LiveData<List<GalleryItem>> = FlickrFetchr().fetchPhotos()
}