package com.example.todoapp

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var selectedTodoData: TodoData
    private var isSelected = false
    private lateinit var title: Editable
    private lateinit var description: Editable
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

        arguments?.let {
            isSelected = it.getBoolean("isSelected", false)
            if (isSelected) {
                selectedTodoData = it.getSerializable("taskModel") as TodoData
                taskBinding.titleEt.setText(selectedTodoData.title)
                taskBinding.descriptionEt.setText(selectedTodoData.description)

                // Set the spinner based on the priority
                when (selectedTodoData.priority) {
                    Priority.High -> taskBinding.spinnerItemsId.setSelection(0)
                    Priority.Medium -> taskBinding.spinnerItemsId.setSelection(1)
                    Priority.Low -> taskBinding.spinnerItemsId.setSelection(2)
                }
            }
        } ?: run {
            isSelected = false
        }


//        if (isSelected) {
//            setActivityTitle("Update Task")
//
//        }
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
                                    R.color.purple_700
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
        if (isSelected) {
            when (item.itemId) {
                R.id.menu_delete -> confirmItemRemoval()
                R.id.menu_save -> updateTask()
            }
        } else {
            when (item.itemId) {
                R.id.menu_add -> insertData()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun updateTask() {
        title = taskBinding.titleEt.text
        description = taskBinding.descriptionEt.text
        val spinnerItem = taskBinding.spinnerItemsId.selectedItem.toString()
        when (selectedTodoData.priority) {
            Priority.High -> taskBinding.spinnerItemsId.setSelection(0)
            Priority.Medium -> taskBinding.spinnerItemsId.setSelection(1)
            Priority.Low -> taskBinding.spinnerItemsId.setSelection(2)
        }
        if (title.toString().isNotEmpty() && description.toString().isNotEmpty()) {
            val updateTodo = TodoData(
                selectedTodoData.id,
                title.toString(),
                parsePriorityString(spinnerItem),
                description.toString()
            )
            todoViewModel.updateTodoData(updateTodo)
            findNavController().navigate(R.id.action_addTaskFragment_to_homeFragment)
        } else {
            Snackbar.make(requireView(), "Title and  Description is required", Snackbar.LENGTH_LONG)
                .show()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return if (isSelected) {
            inflater.inflate(R.menu.update, menu)
        } else {
            inflater.inflate(R.menu.add_fragment, menu)
        }
    }

    private fun insertData() {
        title = taskBinding.titleEt.text
        description = taskBinding.descriptionEt.text
        val spinnerItem = taskBinding.spinnerItemsId.selectedItem.toString()
        if (!title.toString().isNullOrEmpty() && !description.toString().isNullOrEmpty()) {
            todoViewModel.insertTodoData(
                TodoData(
                    0,
                    title.toString(),
                    parsePriorityString(spinnerItem),
                    description.toString()

                )
            )
            findNavController().navigate(R.id.action_addTaskFragment_to_homeFragment)
        } else {
            Snackbar.make(requireView(), "Added Task", Snackbar.LENGTH_SHORT).show()
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


    private fun confirmItemRemoval() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setPositiveButton("YES") { _, _ ->
                todoViewModel.deleteTodo(selectedTodoData)
                Snackbar.make(
                    requireView(),
                    "Successfully deleted $selectedTodoData",
                    Snackbar.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.action_addTaskFragment_to_homeFragment)
            }
            setNegativeButton("NO") { _, _ -> }
            setTitle("DELETE ${selectedTodoData.title}")
            setMessage("Are you sure you want to remove: '${selectedTodoData.title}'?")
            create()
        }
        alertDialog.show()
    }


}