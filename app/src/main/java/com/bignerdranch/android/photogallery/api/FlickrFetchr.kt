package com.bignerdranch.android.photogallery.api

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.photogallery.model.GalleryItem
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "FlickrFetchr"

class FlickrFetchr {
    private val flickrApi: FlickrApi

    init {
        val gson = GsonBuilder()
            .registerTypeAdapter(PhotoResponse::class.java, PhotoDeserializer())
            .create()
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val flickrHomePageRequest: Call<PhotoDeserializer> = flickrApi.fetchPhotos()
        flickrHomePageRequest.enqueue(object : Callback<PhotoDeserializer> {
            override fun onResponse(call: Call<PhotoDeserializer>, response: Response<PhotoDeserializer>) {
                Log.d(TAG, "Response received")
                val flickrResponse: PhotoDeserializer? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isBlank()
                }
                responseLiveData.value = galleryItems
            }

            override fun onFailure(call: Call<PhotoDeserializer>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }
        })
        return responseLiveData
    }
}