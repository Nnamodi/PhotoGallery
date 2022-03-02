package com.bignerdranch.android.photogallery.ui

import android.app.Application
import androidx.lifecycle.*
import com.bignerdranch.android.photogallery.api.FlickrFetchr
import com.bignerdranch.android.photogallery.data.GalleryRepository
import com.bignerdranch.android.photogallery.data.QueryPreferences
import com.bignerdranch.android.photogallery.database.GalleryDatabase
import com.bignerdranch.android.photogallery.model.Gallery
import com.bignerdranch.android.photogallery.model.GalleryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotoGalleryViewModel(private val app: Application) : AndroidViewModel(app) {
    val galleryItemLiveData: LiveData<List<GalleryItem>>
    private val flickrFetchr = FlickrFetchr()
    private val mutableSearchTerm = MutableLiveData<String>()
    val searchTerm: String
        get() = mutableSearchTerm.value ?: ""
    // Database
    val getSearches: LiveData<List<Gallery>>
    private val repository: GalleryRepository
    private val galleryDao = GalleryDatabase.getDatabase(app).galleryDao()

    init {
        mutableSearchTerm.value = QueryPreferences.getStoredQuery(app)
        galleryItemLiveData = Transformations.switchMap(mutableSearchTerm) { searchTerm ->
            if (searchTerm.isBlank()) {
                flickrFetchr.fetchPhotos()
            } else {
                flickrFetchr.searchPhotos(searchTerm)
            }
        }
        // Database
        repository = GalleryRepository(galleryDao)
        getSearches = repository.getSearches
    }

    fun fetchPhotos(query: String = "") {
        QueryPreferences.setStoredQuery(app, query)
        mutableSearchTerm.value = query
    }

    fun addSearch(gallery: Gallery) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSearch(gallery)
        }
    }

    fun removeSearch(gallery: Gallery) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeSearch(gallery)
        }
    }

    fun clearAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAll()
        }
    }
}