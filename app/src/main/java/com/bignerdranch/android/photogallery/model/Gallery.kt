package com.bignerdranch.android.photogallery.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Gallery(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var search: String = ""
)