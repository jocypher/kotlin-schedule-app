package com.example.todoapp.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TodoDao
import com.example.todoapp.data.models.TodoData
import kotlinx.coroutines.launch

class TodoViewModel(private var dao: TodoDao) : ViewModel() {
    val getAllData = dao.getAllTodos()
    val sortedDataHigh = dao.sortByHighPriority()
    val sortedDataLow = dao.sortByLowPriority()

    fun insertTodoData(todoData: TodoData) = viewModelScope.launch {
        dao.insertTodo(todoData)
    }

    fun updateTodoData(todoData: TodoData) = viewModelScope.launch {
        dao.updateTodo(todoData)
    }

    fun deleteTodo(todoData: TodoData) = viewModelScope.launch {
        dao.deleteTodo(todoData)
    }

    fun searchDatabase(searchString: String) = dao.searchDatabase(searchString)


}