package com.example.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.todoapp.databinding.FragmentAddTaskBinding

class AddTaskFragment : Fragment() {
    private lateinit var taskBinding: FragmentAddTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        taskBinding = FragmentAddTaskBinding.inflate(inflater, container, false)
        return taskBinding.root
    }


}