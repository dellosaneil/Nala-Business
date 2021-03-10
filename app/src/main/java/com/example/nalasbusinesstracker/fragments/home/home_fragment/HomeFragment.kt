package com.example.nalasbusinesstracker.fragments.home.home_fragment

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.HomeClothingClicked{

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this) }
    private val TAG = "HomeFragment"


    private val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                binding.homeFragmentAdd.hide()
            } else if (dy < 0) {
                binding.homeFragmentAdd.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.homeFragmentRv.addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        super.onStop()
        binding.homeFragmentRv.removeOnScrollListener(scrollListener)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
        floatingActionButton(view)
    }

    private fun floatingActionButton(view: View) {
        binding.homeFragmentAdd.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.home_addInventory)
        }
    }

    private fun initializeRecyclerView() {
        binding.homeFragmentRv.apply {
            adapter = homeAdapter
            val spanCount = if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 4
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
        observeAdapterChanges()
        observeQueryChanges()
    }

    private fun observeQueryChanges() {
        homeViewModel.queryValues.observe(viewLifecycleOwner) {
            it?.let { queries ->
                homeViewModel.queryClothes(queries[0], queries[1], queries[2], queries[3])
            }
        }
    }

    private fun observeAdapterChanges() {
        homeViewModel.clothingList.observe(viewLifecycleOwner) {
            it?.let { clothes ->
                homeAdapter.updateClothes(clothes)
                binding.homeFragmentRv.smoothScrollToPosition(0)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun clothingClicked(index: Int) {
        Log.i(TAG, "clothingClicked: $index")
    }

}