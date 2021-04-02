package com.example.nalasbusinesstracker.fragments.home.home_fragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nalasbusinesstracker.repositories.ClothingRepository
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(private val clothingRepository: ClothingRepository) :
    ViewModel() {

    private val _clothingList = MutableLiveData<List<Clothes>>()
    var clothingList: LiveData<List<Clothes>> = _clothingList
    private val _category = MutableLiveData<SortedSet<String>>()
    var category : LiveData<SortedSet<String>> = _category
    private val _color = MutableLiveData<SortedSet<String>>()
    var color : LiveData<SortedSet<String>> = _color

    fun initializeData(){
        viewModelScope.launch(IO) {
            val clothingTemp = clothingRepository.retrieveAllClothing()
            val categorySet = sortedSetOf<String>()
            val colorSet = sortedSetOf<String>()
            clothingTemp.collect { clothingList ->
                clothingList.forEach {
                    categorySet.add(it.clothingType)
                    colorSet.add(it.dominantColor)
                }
                withContext(Main) {
                    _clothingList.value = clothingList
                    _category.value = categorySet
                    _color.value = colorSet
                }
            }
        }
    }

    fun queryClothes(type : String, color : String, status : String, code :String){
        val formatType = "%$type%"
        val formatColor = "%$color%"
        val formatStatus = "%$status%"
        val formatCode = "%$code%"
        viewModelScope.launch(IO){
            clothingRepository.queryClothes(formatType, formatColor, formatStatus, formatCode).collect{
                withContext(Main){
                    _clothingList.value = it
                }
            }
        }
    }
}


