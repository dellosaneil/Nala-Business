package com.example.nalasbusinesstracker.fragments.home.home_fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.nalasbusinesstracker.Constants
import com.example.nalasbusinesstracker.GlideApp
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.DialogHomeBinding
import com.example.nalasbusinesstracker.repositories.ClothingRepository
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeDialogFragment(private val clothingDetails : Clothes? = null) : DialogFragment(), View.OnClickListener {

    private var _binding: DialogHomeBinding? = null
    private val binding get() = _binding!!
    private var clothingCopy : Clothes? = null
    private val outStateKey = "clothingKey"

    @Inject
    lateinit var clothingRepository: ClothingRepository


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        clothingDetails?.let{
            clothingCopy = it
        } ?: savedInstanceState?.let{
            clothingCopy = it.getParcelable(outStateKey)
        }
        placeValues()
        listenClickEvents()
    }

    private fun listenClickEvents() {
        binding.homeDialogSold.setOnClickListener(this)
        binding.homeDialogEdit.setOnClickListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(outStateKey, clothingCopy)
    }


    private fun placeValues() {
        clothingCopy?.let{
            binding.homeDialogItemCode.text = it.itemCode
            binding.homeDialogClothingType.text =   getString(R.string.homeDialog_clothingType,it.clothingType)
            binding.homeDialogClothingSize.text = getString(R.string.homeDialog_clothingSize, it.clothingSize.toString())
            binding.homeDialogClothingSellPrice.text = getString(R.string.homeDialog_sellingPrice, it.sellingPrice.toString())
            inputImage(it.imageReference)
        }
    }

    private fun inputImage(imageReference: String) {
        val storageClothingReference =
            Firebase.storage.getReferenceFromUrl("${Constants.FIREBASE_STORAGE_LINK}${imageReference}")
        GlideApp.with(requireContext())
            .load(storageClothingReference)
            .placeholder(R.drawable.ic_cloth_100)
            .into(binding.homeDialogImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.homeDialog_sold -> updateCurrentStatus()
            R.id.homeDialog_edit -> editClothing()
        }
    }

    private fun editClothing() {

    }

    private fun updateCurrentStatus() {
        lifecycleScope.launch(IO){
            clothingCopy?.let{
                clothingRepository.updateStatus("Sold", it.itemCode)
                this@HomeDialogFragment.dismiss()
            }
        }
    }
}