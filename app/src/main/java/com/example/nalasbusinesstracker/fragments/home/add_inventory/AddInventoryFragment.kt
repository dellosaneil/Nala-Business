package com.example.nalasbusinesstracker.fragments.home.add_inventory

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.FragmentAddInventoryBinding
import com.google.android.material.datepicker.MaterialDatePicker


class AddInventoryFragment : Fragment(), View.OnClickListener {
    private var _binding: FragmentAddInventoryBinding? = null
    private val binding get() = _binding!!
    private val TAG = "AddInventoryFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeHints()
        setClickListeners()
    }

    private fun setClickListeners() {
        binding.inventoryPurchaseDate.setOnClickListener(this)
        binding.inventorySave.setOnClickListener(this)
    }

    private fun placeHints() {
        val hintArray = resources.getStringArray(R.array.inventory_hint)
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
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.inventory_save -> saveToInventory()
            R.id.inventory_purchaseDate -> selectPurchaseDate()

        }
    }

    private fun selectPurchaseDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.show(requireActivity().supportFragmentManager, datePicker.toString())
        datePicker.addOnPositiveButtonClickListener {
            Log.i(TAG, "selectPurchaseDate: ${binding.inventoryPurchaseDate.hint}")
            binding.inventoryPurchaseDate.hint = it.toString()
        }
        
        

    }

    private fun saveToInventory() {

    }

}