package com.example.todoapp.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo_data_table")
data class TodoData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    var id: Int,
    @ColumnInfo("title")
    var title: String,
    @ColumnInfo("priority")
    var priority: Priority,
    @ColumnInfo("description")
    var description: String,
    @ColumnInfo("task_status")
    var isCompleted: Boolean = false
)