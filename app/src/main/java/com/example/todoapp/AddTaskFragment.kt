package com.example.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.todoapp.data.TodoDB
import com.example.todoapp.data.models.Priority
import com.example.todoapp.data.models.TodoData
import com.example.todoapp.databinding.FragmentAddTaskBinding
import com.example.todoapp.domain.TodoViewModel
import com.example.todoapp.domain.TodoViewModelFactory
import com.google.android.material.snackbar.Snackbar

class AddTaskFragment : Fragment() {
    private lateinit var taskBinding: FragmentAddTaskBinding
    private lateinit var todoViewModel: TodoViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        taskBinding = FragmentAddTaskBinding.inflate(inflater, container, false)

        val application = requireActivity().application
        val dao = TodoDB.getInstance(application).todoDao()
        val factory = TodoViewModelFactory(dao)
        todoViewModel = ViewModelProvider(this, factory)[TodoViewModel::class.java]

        taskBinding.spinnerItemsId.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    when (position) {
                        0 -> {
                            (view as? TextView)?.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.red
                                )
                            )
                        }

                        1 -> {
                            (view as? TextView)?.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.yellow
                                )
                            )
                        }

                        2 -> {
                            (view as? TextView)?.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.green
                                )
                            )
                        }
                    }
                }


                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Toast.makeText(requireContext(), "whyyyyy", Toast.LENGTH_LONG).show()
                }

            }
        setHasOptionsMenu(true)
        return taskBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            insertData()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.add_fragment, menu)
    }

    private fun insertData() {
        val title = taskBinding.titleEt.text.toString()
        val description = taskBinding.descriptionEt.text.toString()
        val spinnerItem = taskBinding.spinnerItemsId.selectedItem.toString()
        if (title.isNotEmpty() && description.isNotEmpty()) {
            todoViewModel.insertTodoData(
                TodoData(
                    0,
                    title,
                    parsePriorityString(spinnerItem),
                    description

                )
            )
            findNavController().navigate(R.id.action_addTaskFragment_to_homeFragment)
        } else {
            Snackbar.make(requireView(), "Title and  Description is required", Snackbar.LENGTH_LONG)
                .show()
        }


    }


    private fun parsePriorityString(priority: String): Priority {
        return when (priority) {
            "High Priority" -> Priority.High
            "Medium Priority" -> Priority.Medium
            "Low Priority" -> Priority.Low
            else -> Priority.Low
        }
    }


}