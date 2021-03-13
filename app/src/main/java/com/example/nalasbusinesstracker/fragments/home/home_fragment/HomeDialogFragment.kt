package com.example.nalasbusinesstracker.fragments.home.home_fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.nalasbusinesstracker.GlideApp
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.DialogHomeBinding
import com.example.nalasbusinesstracker.room.data_classes.Clothes

class HomeDialogFragment(private val clothingDetails : Clothes?) : DialogFragment() {

    private var _binding: DialogHomeBinding? = null
    private val binding get() = _binding!!

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
        placeValues()
    }

    private fun placeValues() {
        clothingDetails?.let{
            binding.homeDialogClothingType.text = it.clothingType
            binding.homeDialogItemCode.text = it.itemCode
            inputImage(it.imageReference)
        }
    }

    private fun inputImage(imageReference: String) {
        GlideApp.with(requireContext())
            .load(imageReference)
            .placeholder(R.drawable.ic_cloth_100)
            .into(binding.homeDialogImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}