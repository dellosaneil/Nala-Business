package com.example.nalasbusinesstracker.fragments.home.home_fragment

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nalasbusinesstracker.FragmentLifecycle
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.FragmentHomeBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class HomeFragment : FragmentLifecycle(), HomeAdapter.HomeClothingClicked,
    ChipGroup.OnCheckedChangeListener, SearchView.OnQueryTextListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val homeAdapter: HomeAdapter by lazy { HomeAdapter(this) }
    private var chipCategorySelected = 0
    private var chipColorSelected = 0
    private var filterArray = mutableListOf("", "")
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

    }

    override fun onStart() {
        super.onStart()
        homeViewModel.initializeData()
        attachChipGroupObservers()
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

    private val categoryObserver = Observer<SortedSet<String>> {
        it?.let { category ->
            repeat(category.size) {
                Chip(requireContext()).apply {
                    id = it
                    isCheckable = true
                    isChipIconVisible = false
                    isCheckedIconVisible = false
                    setChipBackgroundColorResource(R.color.mtrl_choice_chip_background_color)
                    setRippleColorResource(R.color.mtrl_choice_chip_ripple_color)
                    text = category.elementAt(it)
                    binding.homeFragmentCategoryGroup.addView(this)
                    categoryArray.add(category.elementAt(it))
                }
            }
        }
    }

    private val colorObserver = Observer<SortedSet<String>> {
        it?.let { color ->
            repeat(color.size) {
                Chip(requireContext()).apply {
                    id = it
                    isCheckable = true
                    isChipIconVisible = false
                    isCheckedIconVisible = false
                    setChipBackgroundColorResource(R.color.mtrl_choice_chip_background_color)
                    setRippleColorResource(R.color.mtrl_choice_chip_ripple_color)
                    text = color.elementAt(it)
                    binding.homeFragmentColorGroup.addView(this)
                    colorArray.add(color.elementAt(it))
                }
            }
        }
    }

    private fun removeChipGroupObservers(){
        homeViewModel.category.removeObserver(categoryObserver)
        homeViewModel.color.removeObserver(colorObserver)
    }


    private fun attachChipGroupObservers() {
        binding.homeFragmentCategoryGroup.setOnCheckedChangeListener(this)
        binding.homeFragmentColorGroup.setOnCheckedChangeListener(this)
        colorArray.add("")
        categoryArray.add("")
        homeViewModel.category.observe(viewLifecycleOwner, categoryObserver)
        homeViewModel.color.observe(viewLifecycleOwner, colorObserver)
    }

    override fun onPause() {
        super.onPause()
        removeChipGroupObservers()
    }

    override fun onResume() {
        super.onResume()
        binding.homeFragmentRv.addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        super.onStop()
        binding.homeFragmentRv.removeOnScrollListener(scrollListener)
        colorArray.clear()
        categoryArray.clear()
        homeViewModel.clearData()
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

    }

    private fun calculateNumberColumns(
        context: Context
    ): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidthDp = displayMetrics.widthPixels / displayMetrics.density
        return (screenWidthDp / 162 + 0.5).toInt()
    }

    private fun updateCategoryFilter(checkedId: Int) {
        chipCategorySelected = checkedId + 1
        homeViewModel.queryClothes(
            categoryArray[chipCategorySelected],
            colorArray[chipColorSelected],
            filterArray[0],
            filterArray[1]
        )
    }

    override fun onCheckedChanged(group: ChipGroup?, checkedId: Int) {
        when (group?.id) {
            R.id.homeFragment_categoryGroup -> updateCategoryFilter(checkedId)
            R.id.homeFragment_colorGroup -> updateColorFilter(checkedId)
        }

        binding.homeFragmentAdd.show()
    }

    private fun updateColorFilter(checkedId: Int) {
        chipColorSelected = checkedId + 1
        homeViewModel.queryClothes(
            categoryArray[chipCategorySelected],
            colorArray[chipColorSelected],
            filterArray[0],
            filterArray[1]
        )
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        query?.let {
            filterArray[1] = it
            homeViewModel.queryClothes(
                categoryArray[chipCategorySelected],
                colorArray[chipColorSelected],
                filterArray[0],
                filterArray[1]
            )
            return true
        }
        return false
    }
}