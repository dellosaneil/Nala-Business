package com.example.nalasbusinesstracker.fragments.home.home_fragment

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
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
import com.example.nalasbusinesstracker.room.data_classes.Clothes
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
    private val homeAdapter: HomeAdapter by lazy {
        HomeAdapter(this)
    }
    private var chipCategorySelected = 0
    private var chipColorSelected = 0
    private val filterArray = arrayOf("", "")
    private val categoryArray = mutableSetOf<String>()
    private val colorArray = mutableSetOf<String>()
    private var currentList = listOf<Clothes>()
    private var alertDialogClothing: Clothes? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(savedInstanceState == null) {
            initializeRecyclerView()
            floatingActionButton(view)
            homeViewModel.initializeData()
        }
    }

    override fun onStart() {
        super.onStart()
        prepareChipGroup()
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
            if (dy > 0 && binding.homeFragmentAdd.visibility == View.VISIBLE) {
                binding.homeFragmentAdd.hide()
            } else if (dy < 0 && binding.homeFragmentAdd.visibility == View.GONE) {
                binding.homeFragmentAdd.show()
            }
        }
    }

    private val categoryObserver = Observer<SortedSet<String>> {
        binding.homeFragmentCategoryGroup.removeAllViews()
        if(homeViewModel.category.hasObservers()) homeViewModel.category.removeObservers(viewLifecycleOwner)
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
        binding.homeFragmentColorGroup.removeAllViews()
        if(homeViewModel.color.hasObservers()) homeViewModel.color.removeObservers(viewLifecycleOwner)
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


    private fun prepareChipGroup() {
        binding.homeFragmentCategoryGroup.setOnCheckedChangeListener(this)
        binding.homeFragmentColorGroup.setOnCheckedChangeListener(this)
        colorArray.add("")
        categoryArray.add("")
        homeViewModel.category.observe(viewLifecycleOwner, categoryObserver)
        homeViewModel.color.observe(viewLifecycleOwner, colorObserver)
    }


    override fun onResume() {
        super.onResume()
        binding.homeFragmentRv.addOnScrollListener(scrollListener)
    }

    override fun onStop() {
        super.onStop()
        binding.homeFragmentRv.removeOnScrollListener(scrollListener)
        clearChipGroupValues()
    }

    private fun clearChipGroupValues(){
        colorArray.clear()
        categoryArray.clear()
        binding.homeFragmentCategoryGroup.setOnCheckedChangeListener(null)
        binding.homeFragmentColorGroup.setOnCheckedChangeListener(null)
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
                currentList = clothes
                homeAdapter.updateClothes(clothes)
                binding.homeFragmentRv.smoothScrollToPosition(0)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        chipCategorySelected = 0
        chipColorSelected = 0
        _binding = null
    }

    override fun clothingClicked(index: Int) {
        alertDialogClothing = currentList[index]
        val dialog = HomeDialogFragment(alertDialogClothing)
        dialog.show(requireParentFragment().parentFragmentManager, "Tag")
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
            categoryArray.elementAt(chipCategorySelected),
            colorArray.elementAt(chipColorSelected),
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
            categoryArray.elementAt(chipCategorySelected),
            colorArray.elementAt(chipColorSelected),
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
                categoryArray.elementAt(chipCategorySelected),
                colorArray.elementAt(chipColorSelected),
                filterArray[0],
                filterArray[1]
            )
            return true
        }
        return false
    }
}













