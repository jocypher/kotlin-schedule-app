package com.example.todoapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.data.models.TodoData

@Dao
interface TodoDao {
    @Query("SELECT * FROM todo_data_table ORDER BY id ASC")
    fun getAllTodos(): LiveData<List<TodoData>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todoData: TodoData)

    @Update
    suspend fun updateTodo(todoData: TodoData)

    @Delete
    suspend fun deleteTodo(todoData: TodoData)

    @Query("DELETE FROM todo_data_table")
    fun deleteAllTodo()

    @Query(
        "SELECT * FROM todo_data_table ORDER BY CASE " +
                "WHEN priority LIKE 'H%' THEN 1 " +
                "WHEN priority LIKE 'M%' THEN 2 " +
                "WHEN priority LIKE 'L%' THEN 3 END"
    )
    fun sortByHighPriority(): LiveData<List<TodoData>>

    @Query(
        "SELECT * FROM todo_data_table ORDER BY CASE " +
                "WHEN priority LIKE 'L%' THEN 1 " +
                "WHEN priority LIKE 'M%' THEN 2 " +
                "WHEN priority LIKE 'H%' THEN 3 END"
    )
    fun sortByLowPriority(): LiveData<List<TodoData>>


    @Query("SELECT * FROM todo_data_table WHERE title LIKE :searchQuery ")
    fun searchDatabase(searchQuery: String): LiveData<List<TodoData>>


}