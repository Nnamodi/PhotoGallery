package com.bignerdranch.android.photogallery.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Gallery(
    @PrimaryKey
    val id: Int,
    val search: String
)