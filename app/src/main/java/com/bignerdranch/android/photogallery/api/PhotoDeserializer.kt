package com.bignerdranch.android.photogallery.api

import com.bignerdranch.android.photogallery.model.GalleryItem
import com.google.gson.*
import java.lang.reflect.Type

class PhotoDeserializer : JsonDeserializer<PhotoResponse> {
    lateinit var photos: PhotoResponse

    override fun deserialize(
        json: JsonElement, type: Type?, context: JsonDeserializationContext?
    ): PhotoResponse {
        val jObject = json as JsonObject
        val jArray = jObject["photo"] as JsonArray
        val photoResponse = PhotoResponse()
        val flickrPhotos: MutableList<GalleryItem> = mutableListOf()
        jArray.forEach {
            val gallery = it as JsonObject
            val album = GalleryItem(
                gallery["title"].asString,
                gallery["id"].asString,
                gallery["url_s"].asString
            )
            flickrPhotos.add(album)
        }
        photoResponse.galleryItems = flickrPhotos
        return photoResponse
    }
}

/** Based on a challenge. */