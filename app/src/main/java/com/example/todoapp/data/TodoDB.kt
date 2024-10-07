package com.example.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.todoapp.data.models.TodoData

@Database(entities = [TodoData::class], version = 1, exportSchema = false)
abstract class TodoDB : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: TodoDB? = null
        fun getInstance(context: Context): TodoDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TodoDB::class.java,
                        "todo_db"
                    ).build()
                }
                return instance
            }
        }
    }

}