package com.example.todoapp.domain

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.todoapp.data.TodoDB

class SharedViewModel(private var application: Application) : ViewModel() {
    private val dao = TodoDB.getInstance(application).todoDao()
    val factory = TodoViewModelFactory(dao)
}