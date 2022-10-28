package com.example.storyapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.Model.RemoteKey

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllRemoteKey(remoteKey: List<RemoteKey>)

    @Query("SELECT * FROM remote_key WHERE id = :id")
    suspend fun getAllRemoteKeyId(id: String): RemoteKey?

    @Query("DELETE FROM remote_key")
    suspend fun deleteAllRemoteKey()
}