package com.example.storyapp.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


@Entity(tableName = "remote_key")
class RemoteKey(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("prevKe")
    val prevKey: Int?,
    @field:SerializedName("nextKey")
    val nextKey: Int?
)
