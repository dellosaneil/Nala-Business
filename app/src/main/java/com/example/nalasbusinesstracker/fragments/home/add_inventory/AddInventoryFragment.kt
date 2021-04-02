package com.example.nalasbusinesstracker.fragments.home.add_inventory

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
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
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.nalasbusinesstracker.Constants.DATA_STORE_NAME
import com.example.nalasbusinesstracker.Constants.LATEST_DATE_KEY
import com.example.nalasbusinesstracker.Constants.STORAGE_PATH
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.FragmentAddInventoryBinding
import com.example.nalasbusinesstracker.databinding.LayoutEditTextBinding
import com.example.nalasbusinesstracker.databinding.LayoutExposedDropDownBinding
import com.example.nalasbusinesstracker.repositories.ClothingRepository
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
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
    private val dataStore: DataStore<Preferences> by lazy {
        requireContext().createDataStore(
            DATA_STORE_NAME
        )
    }
    private val dateKey: Preferences.Key<Long> by lazy { longPreferencesKey(LATEST_DATE_KEY) }
    private val bundleArray = arrayOf(
        "itemCode",
        "clothingType",
        "dominantColor",
        "supplierName",
        "purchasePrice",
        "sellingPrice",
        "clothingSize",
        "imageUri"
    )
    private val storage: StorageReference by lazy { Firebase.storage.reference }

    private val userInputArray = arrayOf<Any>("", 0.0, 0.0, 0.0, "", "", "")
    private var uriImage: Uri? = null
    private var datePurchased = 0L
    private var editTextArray = arrayOf<LayoutEditTextBinding>()
    private var exposedDropDownArray = arrayOf<LayoutExposedDropDownBinding>()
    private var toast: Toast? = null

    @Inject
    lateinit var repository: ClothingRepository

    private val retrieveImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let {
            insertImage(it)
        }
    }

    private fun initializeDropDownMenu(){
        lifecycleScope.launch(Main){
            val items = listOf(repository.getDistinctClothingType(), repository.getDistinctDominantColor(), repository.getDistinctSupplierName())
            repeat(exposedDropDownArray.size){indexNumber ->
                items[indexNumber].collect{
                    val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
                    (exposedDropDownArray[indexNumber].exposedDropDownLayout.editText as? AutoCompleteTextView)?.setAdapter(adapter)
                }
            }
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
        initializeViewArray()
        initializeEditText()
        setClickListeners()
        restorePreviousDate()
        initializeDropDownMenu()
        listenToNavigationIcon(view)
    }

    private fun listenToNavigationIcon(view: View) {
        binding.inventoryToolbar.setNavigationOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }
    }


    private fun initializeViewArray() {
        editTextArray = arrayOf(
            binding.inventoryItemCode,
            binding.inventoryPurchasePrice,
            binding.inventorySellingPrice,
            binding.inventoryClothingSize
        )
        exposedDropDownArray =
            arrayOf(
                binding.inventoryClothingType,
                binding.inventoryDominantColor,
                binding.inventorySupplierName
            )
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
        binding.inventoryImage.setOnClickListener(this)
    }

    private fun initializeEditText() {
        val editTextHint = resources.getStringArray(R.array.inventory_editText_hint)
        val editTextInputType = arrayOf(
            InputType.TYPE_CLASS_TEXT,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
        )
        val exposedTextHint = resources.getStringArray(R.array.inventory_exposedMenu_hint)
        val exposedTextInputType = arrayOf(InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_TEXT)

        repeat(editTextArray.size) {
            editTextArray[it].editTextLayout.hint = editTextHint[it]
            editTextArray[it].editTextInput.inputType = editTextInputType[it]
        }

        repeat(exposedDropDownArray.size) {
            exposedDropDownArray[it].exposedDropDownLayout.hint = exposedTextHint[it]
            exposedDropDownArray[it].exposedDropDownTextView.inputType = exposedTextInputType[it]
        }
        addTextListeners()
    }

    private fun addTextListeners() {
        repeat(editTextArray.size) {
            editTextArray[it].editTextInput.doOnTextChanged { text, _, _, _ ->
                text?.let { charSequence ->
                    try {
                        val temp = charSequence.toString().toDouble()
                        userInputArray[it] = temp
                    } catch (e: Exception) {
                        val temp = charSequence.toString()
                        userInputArray[it] = temp
                    }
                }
            }
        }
        repeat(exposedDropDownArray.size) {
            exposedDropDownArray[it].exposedDropDownTextView.doOnTextChanged { text, _, _, _ ->
                userInputArray[it + 4] = text.toString()
            }
        }
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
        datePurchased = milliseconds
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


    private fun handleToastMessage(message: String) {
        toast?.let {
            toast?.cancel()
            toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
            toast!!.show()
        } ?: run {
            toast = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
            toast!!.show()
        }
    }

    private val String.capitalizeWords
        get() = this.toLowerCase(Locale.ROOT).split(" ")
            .joinToString(" ") { it.capitalize(Locale.ROOT) }

    private fun saveToInventory() {
        lifecycleScope.launch(IO) {
            if (checkValues()) {
                saveToStorage()
                val clothingData = Clothes(
                    userInputArray[0].toString(),
                    userInputArray[4].toString().capitalizeWords,
                    userInputArray[5].toString().capitalizeWords,
                    userInputArray[1] as Double,
                    userInputArray[2] as Double,
                    datePurchased,
                    "Available",
                    "${STORAGE_PATH}${userInputArray[0]}",
                    userInputArray[3] as Double,
                    userInputArray[6].toString()
                )
                repository.insertClothing(clothingData)
                withContext(Main) {
                    handleToastMessage(getString(R.string.inventory_finished_upload))
                    clearValues()
                }
            }
        }
    }

//    val itemCode: String,
//    val clothingType: String,
//    val dominantColor: String,
//    val purchasePrice: Double,
//    val sellingPrice : Double,
//    val purchaseDate: Long,
//    val currentStatus: String,
//    val imageReference: String,
//    val clothingSize : Double,
//    val supplierName: String? = null,
//    val storageTime : Long = System.currentTimeMillis()


    private fun clearValues() {
        val initialValues = arrayOf("", 0.0, 0.0, 0.0, "", "", "")
        repeat(initialValues.size) {
            userInputArray[it] = initialValues[it]
        }
        uriImage = null
        repeat(editTextArray.size) {
            editTextArray[it].editTextInput.text = null
        }
        repeat(exposedDropDownArray.size){
            exposedDropDownArray[it].exposedDropDownTextView.text = null
        }

        binding.inventoryImage.setImageDrawable(null)
        binding.inventoryImage.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.gray
            )
        )
    }


    private suspend fun checkValues(): Boolean {
        var canSave = true
        withContext(Main) {
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
                binding.inventoryPurchaseDate.error =
                    resources.getString(R.string.inventory_required)
                canSave = false
            }
        }
        val job = lifecycleScope.async(IO) {
            val checkCode = repository.checkCode(editTextArray[0].editTextInput.text.toString())
            withContext(Main) {
                if (checkCode != 0) {
                    editTextArray[0].editTextLayout.error = getString(R.string.inventory_code_used)
                    canSave = false
                } else {
                    editTextArray[0].editTextLayout.error = null
                }
            }
        }
        job.await()
        if (uriImage == null) {
            canSave = false
        }
        return canSave
    }

    private suspend fun saveToStorage() {
        withContext(Main) {
            handleToastMessage(getString(R.string.inventory_uploading))
        }
        storage.child("${STORAGE_PATH}${userInputArray[0]}").putFile(uriImage!!)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}














