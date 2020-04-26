package com.example.photoapp.datahandling

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Photo::class , Album::class), version = 1, exportSchema = false)
abstract class PhotoDatabase: RoomDatabase() {
    abstract fun photoDAO(): PhotoDAO
    //builds room database with DAO and tables
    companion object {
        private var instance: PhotoDatabase? = null

        fun getDatabase(ctx: Context): PhotoDatabase {
            var tmpInstance = instance
            if (tmpInstance == null) {
                tmpInstance = Room.databaseBuilder(
                    ctx.applicationContext,
                    PhotoDatabase::class.java,
                    "photoDatabase"
                ).build()
                instance = tmpInstance
            }
            return tmpInstance
        }
    }
}