package com.example.todoapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.data.TodoDB
import com.example.todoapp.data.models.TodoData
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.domain.TodoViewModel
import com.example.todoapp.domain.TodoViewModelFactory
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), SearchView.OnQueryTextListener {
    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var recyclerViewAdapter: TodoRecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var selectedTodoData: TodoData
    private lateinit var todoViewModel: TodoViewModel
    private var isSelected = false
    private var searchJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        val application = requireActivity().application
        val dao = TodoDB.getInstance(application).todoDao()
        val factory = TodoViewModelFactory(dao)
        todoViewModel = ViewModelProvider(this, factory)[TodoViewModel::class.java]
        homeBinding.fabId.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addTaskFragment)
        }
        setHasOptionsMenu(true)
        initRecyclerView()
        return homeBinding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragment, menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as SearchView
        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(this)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_priority_high -> todoViewModel.sortedDataHigh.observe(
                viewLifecycleOwner,
                Observer {
                    recyclerViewAdapter.setTodo(it)
                    recyclerViewAdapter.notifyDataSetChanged()
                })

            R.id.menu_priority_low -> todoViewModel.sortedDataLow.observe(
                viewLifecycleOwner,
                Observer {
                    recyclerViewAdapter.setTodo(it)
                    recyclerViewAdapter.notifyDataSetChanged()
                })

            R.id.menu_delete_all -> deleteAllTask()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        recyclerView = homeBinding.recView
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = SlideInUpAnimator().apply { addDuration = 300 }
        recyclerViewAdapter = TodoRecyclerViewAdapter { selectedTodoData: TodoData ->
            onItemSelected(selectedTodoData)
        }
        recyclerView.adapter = recyclerViewAdapter
        displayList()
    }


    private fun displayList() {
        todoViewModel.getAllData.observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter.setTodo(it)
            recyclerViewAdapter.notifyDataSetChanged()
        })
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchDb(query)
            Log.d("SearchQuery1", query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchJob?.cancel()
            searchJob = coroutineScope.launch {
                delay(500)
                searchDb(query)
                Log.d("SearchQuery", query)
            }
        }
        return true
    }

    private fun searchDb(query: String) {
        val searchQuery = "%$query"
        todoViewModel.searchDatabase(searchQuery).observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter.setTodo(it)
            recyclerViewAdapter.notifyDataSetChanged()
        })
    }

    private fun onItemSelected(todoData: TodoData) {
        selectedTodoData = todoData
        isSelected = true
        val bundle = bundleOf(
            "taskModel" to selectedTodoData,
            "isSelected" to isSelected
        )

        Snackbar.make(
            requireView(),
            "Testing to see if selected $selectedTodoData",
            Snackbar.LENGTH_SHORT
        ).show()
        if (isSelected) {
            findNavController().navigate(R.id.action_homeFragment_to_addTaskFragment, bundle)
        }

    }

    private fun deleteAllTask() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setPositiveButton("YES") { _, _ ->
                todoViewModel.deleteAllTodo()
                Snackbar.make(
                    requireView(),
                    "Successfully deleted all task",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            setNegativeButton("NO") { _, _ -> }
            setTitle("DELETE All Task")
            setMessage("Are you sure you want to delete all task?")
            create()
        }
        alertDialog.show()
    }

}