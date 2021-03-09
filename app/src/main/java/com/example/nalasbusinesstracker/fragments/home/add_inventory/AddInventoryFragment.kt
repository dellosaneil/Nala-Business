package com.example.nalasbusinesstracker.fragments.home.add_inventory

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.FragmentAddInventoryBinding
import com.example.nalasbusinesstracker.databinding.FragmentHomeBinding


class AddInventoryFragment : Fragment() {
    private var _binding: FragmentAddInventoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}