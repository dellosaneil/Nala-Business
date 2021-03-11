package com.example.nalasbusinesstracker.fragments.home.home_fragment

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.FragmentHomeBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment(), HomeAdapter.HomeClothingClicked,
    ChipGroup.OnCheckedChangeListener, SearchView.OnQueryTextListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this) }
    private val TAG = "HomeFragment"
    private var chipSelected = 0
    private var filterArray = mutableListOf("", "", "")
    private val categoryArray = mutableListOf<String>()
    private val colorArray = mutableListOf<String>()

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
        dynamicallyAllocateChips()
        initializeSearchView()
    }

    private fun initializeSearchView() {
        val searchMenu = binding.homeFragmentToolBar.menu.findItem(R.id.homeMenu_search)
        (searchMenu?.actionView as? SearchView)?.apply {
            queryHint = getString(R.string.home_searchViewHint)
            setOnQueryTextListener(this@HomeFragment)
        }
    }


    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0) {
                binding.homeFragmentAdd.hide()
            } else if (dy < 0) {
                binding.homeFragmentAdd.show()
            }
        }
    }


    private fun dynamicallyAllocateChips() {
        binding.homeFragmentCategoryGroup.setOnCheckedChangeListener(this)
        categoryArray.add("")
        colorArray.add("")
        val chipGroup =
            arrayOf(binding.homeFragmentCategoryGroup, binding.homeFragmentColorGroup)
        val chips = arrayOf(homeViewModel.category, homeViewModel.color)
        repeat(chips.size) { outerIndex ->
            chips[outerIndex].observe(viewLifecycleOwner) { categoryList ->
                categoryList?.let { chipNames ->
                    repeat(chipNames.size) {
                        Chip(requireContext()).apply {
                            id = it
                            isCheckable = true
                            isChipIconVisible = false
                            isCheckedIconVisible = false
                            setChipBackgroundColorResource(R.color.mtrl_choice_chip_background_color)
                            setRippleColorResource(R.color.mtrl_choice_chip_ripple_color)
                            text = chipNames.elementAt(it)
                            chipGroup[outerIndex].addView(this)
                            categoryArray.add(chipNames.elementAt(it))
                        }
                    }
                }
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


    private fun floatingActionButton(view: View) {
        binding.homeFragmentAdd.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.home_addInventory)
        }
    }

    private fun initializeRecyclerView() {
        binding.homeFragmentRv.apply {
            adapter = homeAdapter
            val spanCount = calculateNumberColumns(requireContext())
            layoutManager = GridLayoutManager(requireContext(), spanCount)
        }
        observeAdapterChanges()
        observeQueryChanges()
    }

    private fun observeQueryChanges() {
        homeViewModel.queryValues.observe(viewLifecycleOwner) {
            it?.let { queries ->
                homeViewModel.queryClothes(queries[0], queries[1], queries[2], queries[3])
                binding.homeFragmentRv.smoothScrollToPosition(0)
            }
        }
    }

    private fun observeAdapterChanges() {
        homeViewModel.clothingList.observe(viewLifecycleOwner) {
            it?.let { clothes ->
                homeAdapter.updateClothes(clothes)
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

    private fun calculateNumberColumns(
        context: Context
    ): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / 162 + 0.5).toInt()
    }

    override fun onCheckedChanged(group: ChipGroup?, checkedId: Int) {
        chipSelected = checkedId + 1
        homeViewModel.queryClothes(
            categoryArray[chipSelected],
            filterArray[0],
            filterArray[1],
            filterArray[2]
        )
        binding.homeFragmentAdd.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let {
            filterArray[2] = it
            homeViewModel.queryClothes(
                categoryArray[chipSelected],
                filterArray[0],
                filterArray[1],
                filterArray[2]
            )
            return true
        }
        return false
    }
}