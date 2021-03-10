package com.example.nalasbusinesstracker.fragments.home.add_inventory

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.nalasbusinesstracker.Constants.DATA_STORE_NAME
import com.example.nalasbusinesstracker.Constants.LATEST_DATE_KEY
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.FragmentAddInventoryBinding
import com.example.nalasbusinesstracker.databinding.LayoutEditTextBinding
import com.example.nalasbusinesstracker.repositories.ClothingRepository
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class AddInventoryFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAddInventoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var dateKey: Preferences.Key<Long>
    private val bundleArray = arrayOf(
        "itemCode",
        "dominantColor",
        "purchasePrice",
        "sellingPrice",
        "clothingSize",
        "supplierName",
        "imageUri"
    )
    private val valuesArray = arrayOf<Any>("", "", 0.0, 0.0, 0.0, "")
    private var uriImage: Uri? = null

    private val TAG = "AddInventoryFragment"

    @Inject
    lateinit var repository: ClothingRepository

    private val retrieveImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            insertImage(it)
        }
    }

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
        savedInstanceState?.let {
            retrieveFromConfigurationChange(it)
        }

    }


    private fun retrieveFromConfigurationChange(savedInstanceState: Bundle) {
        val editTextArray = arrayOf(
            binding.inventoryItemCode,
            binding.inventoryDominantColor,
            binding.inventoryPurchasePrice,
            binding.inventorySellingPrice,
            binding.inventoryClothingSize,
            binding.inventorySupplierName
        )
        repeat(editTextArray.size) {
            try {
                val temp = savedInstanceState.getDouble(bundleArray[it])
                editTextArray[it].editTextInput.setText(temp.toString())
            } catch (e: ClassCastException) {
                val temp = savedInstanceState.getString(bundleArray[it])
                editTextArray[it].editTextInput.setText(temp)
            }
        }

        savedInstanceState.getParcelable<Uri>(bundleArray[6])?.let {
            insertImage(it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        repeat(bundleArray.size - 1) {
            try {
                val temp = valuesArray[it] as Double
                outState.putDouble(bundleArray[it], temp)
            } catch (e: ClassCastException) {
                val temp = valuesArray[it] as String
                outState.putString(bundleArray[it], temp)
            }
        }
        uriImage?.let {
            outState.putParcelable(bundleArray[6], it)
        }
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

    private fun checkValues(): Boolean {
        var canSave = true
        val editTextArray = arrayOf(
            binding.inventoryItemCode,
            binding.inventoryDominantColor,
            binding.inventoryPurchasePrice,
            binding.inventorySellingPrice,
            binding.inventoryClothingSize,
            binding.inventorySupplierName
        )
        repeat(editTextArray.size) {
            if (editTextArray[it].editTextInput.text?.isEmpty() == true) {
                editTextArray[it].editTextLayout.error =
                    resources.getString(R.string.inventory_required)
                canSave = false
            } else {
                editTextArray[it].editTextLayout.error = null
            }
        }
        if (binding.inventoryPurchaseDate.hint == resources.getString(R.string.inventory_calendar)) {
            binding.inventoryPurchaseDate.error = resources.getString(R.string.inventory_required)
            canSave = false
        }
        lifecycleScope.launch(IO) {
            val checkCode = repository.checkCode(editTextArray[0].editTextInput.text.toString())
            withContext(Main) {
                if (!checkCode.equals(0)) {
                    editTextArray[0].editTextLayout.error = getString(R.string.inventory_code_used)
                    canSave = false
                } else {
                    editTextArray[0].editTextLayout.error = null
                }
            }
        }
        return canSave
    }


    private fun setClickListeners() {
        binding.inventoryPurchaseDate.setOnClickListener(this)
        binding.inventorySave.setOnClickListener(this)
        binding.inventoryImage.setOnClickListener(this)
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
        editTextListeners(editTextArray)
    }

    private fun editTextListeners(editTextArray: Array<LayoutEditTextBinding>) {
        repeat(editTextArray.size) {
            editTextArray[it].editTextInput.doOnTextChanged { text, _, _, _ ->
                text?.let { charSequence ->
                    try {
                        val temp = charSequence.toString().toDouble()
                        valuesArray[it] = temp
                    } catch (e: Exception) {
                        val temp = charSequence.toString()
                        valuesArray[it] = temp
                    }
                }
            }
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
            R.id.inventory_image -> retrieveImage.launch("image/*")
        }
    }


    private fun insertImage(uri: Uri) {
        uriImage = uri
        Glide.with(requireContext())
            .load(uri)
            .into(binding.inventoryImage)

        binding.inventoryImage.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.transparent
            )
        )
    }

    private fun changeDateHint(milliseconds: Long) {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        val dateString: String = simpleDateFormat.format(milliseconds)
        binding.inventoryPurchaseDate.hint = dateString
    }

    private fun saveDateDataStore(milliseconds: Long) {
        lifecycleScope.launch(IO) {
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
        if (checkValues()) {

        }
    }
}