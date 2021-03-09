package com.example.nalasbusinesstracker.fragments.home.add_inventory

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.nalasbusinesstracker.Constants.DATA_STORE_NAME
import com.example.nalasbusinesstracker.Constants.LATEST_DATE_KEY
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.FragmentAddInventoryBinding
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class AddInventoryFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAddInventoryBinding? = null
    private val binding get() = _binding!!
    private val TAG = "AddInventoryFragment"
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dateKey: Preferences.Key<Long>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeEditText()
        setClickListeners()
        initializeDataStore()
    }

    private fun initializeDataStore() {
        dataStore = requireContext().createDataStore(DATA_STORE_NAME)
        dateKey = longPreferencesKey(LATEST_DATE_KEY)
        restorePreviousDate()
    }

    private fun restorePreviousDate() {
        lifecycleScope.launch(IO) {
            val preferences = dataStore.data.first()
            preferences[dateKey]?.let {
                changeDateHint(it)
            }
        }
    }

    private fun setClickListeners() {
        binding.inventoryPurchaseDate.setOnClickListener(this)
        binding.inventorySave.setOnClickListener(this)


    }

    private fun initializeEditText() {
        val hintArray = resources.getStringArray(R.array.inventory_hint)
        val typeArray = arrayOf(
            InputType.TYPE_CLASS_TEXT,
            InputType.TYPE_CLASS_TEXT,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            InputType.TYPE_CLASS_TEXT
        )
        val editTextArray = arrayOf(
            binding.inventoryItemCode,
            binding.inventoryDominantColor,
            binding.inventoryPurchasePrice,
            binding.inventorySellingPrice,
            binding.inventoryClothingSize,
            binding.inventorySupplierName
        )
        repeat(editTextArray.size) {
            editTextArray[it].editTextLayout.hint = hintArray[it]
            editTextArray[it].editTextInput.inputType = typeArray[it]
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.inventory_save -> saveToInventory()
            R.id.inventory_purchaseDate -> selectPurchaseDate()

        }
    }

    private fun changeDateHint(milliseconds: Long) {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        val dateString: String = simpleDateFormat.format(milliseconds)
        binding.inventoryPurchaseDate.hint = dateString
    }

    private fun saveDateDataStore(milliseconds: Long){
        lifecycleScope.launch(IO){
            dataStore.edit {
                it[dateKey] = milliseconds
            }
        }
    }


    private fun selectPurchaseDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.show(requireActivity().supportFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {
            it?.let { time ->
                changeDateHint(time)
                saveDateDataStore(time)
            }
        }
    }

    private fun saveToInventory() {

    }


}