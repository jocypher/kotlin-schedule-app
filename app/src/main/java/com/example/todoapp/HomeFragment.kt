package com.example.todoapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.todoapp.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var homeBinding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        homeBinding.fabId.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_addTaskFragment)
        }
        return homeBinding.root
    }


}