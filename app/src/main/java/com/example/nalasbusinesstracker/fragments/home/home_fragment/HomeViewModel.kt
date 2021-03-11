package com.example.nalasbusinesstracker.fragments.home.home_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nalasbusinesstracker.repositories.ClothingRepository
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(private val clothingRepository: ClothingRepository) :
    ViewModel() {

    private val _clothingList = MutableLiveData<List<Clothes>>()
    var clothingList: LiveData<List<Clothes>> = _clothingList
    private val _category = MutableLiveData<SortedSet<String>>()
    var category : LiveData<SortedSet<String>> = _category

    private val _queryValues = MutableLiveData<List<String>>()
    var queryValues : LiveData<List<String>> = _queryValues

    init {
        viewModelScope.launch(IO) {
            val clothingTemp = clothingRepository.retrieveAllClothing()
            val sortedSet = sortedSetOf<String>()
            clothingTemp.forEach { sortedSet.add(it.clothingType) }
            withContext(Main) {
                _clothingList.value = clothingTemp
                _queryValues.value = listOf("","","","")
                _category.value = sortedSet
            }
        }
    }

    fun queryClothes(type : String, color : String, status : String, code :String){
        val formatType = "%$type%"
        val formatColor = "%$color%"
        val formatStatus = "%$status%"
        val formatCode = "%$code%"
        viewModelScope.launch(IO){
            val clothingTemp = clothingRepository.queryClothes(formatType, formatColor, formatStatus, formatCode)
            withContext(Main){
                _clothingList.value = clothingTemp
            }
        }
    }
}


