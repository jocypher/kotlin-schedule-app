package com.example.todoapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.todoapp.data.models.TodoData
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.domain.SharedViewModel
import com.example.todoapp.domain.TodoViewModel
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
    private lateinit var todoData: TodoData
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var searchJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        val factory = sharedViewModel.factory
        val todoViewModel = ViewModelProvider(this, factory)[TodoViewModel::class.java]
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

        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRecyclerView() {
        recyclerView = homeBinding.recView
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = SlideInUpAnimator().apply { addDuration = 300 }
        recyclerViewAdapter = TodoRecyclerViewAdapter()
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


}