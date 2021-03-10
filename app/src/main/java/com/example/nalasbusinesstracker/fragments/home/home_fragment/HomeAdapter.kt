package com.example.nalasbusinesstracker.fragments.home.home_fragment

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.nalasbusinesstracker.Constants.FIREBASE_STORAGE_LINK
import com.example.nalasbusinesstracker.GlideApp
import com.example.nalasbusinesstracker.R
import com.example.nalasbusinesstracker.databinding.ListHomeBinding
import com.example.nalasbusinesstracker.room.data_classes.Clothes
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class HomeAdapter(private val listener: HomeClothingClicked) :
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>() {
    private var clothes = listOf<Clothes>()


    fun updateClothes(newClothes: List<Clothes>) {
        val oldList = clothes
        val diffResult = DiffUtil.calculateDiff(HomeDiffUtil(oldList, newClothes))
        clothes = newClothes
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val binding = ListHomeBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HomeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val tempClothing = clothes[position]
        holder.bind(tempClothing)
    }

    override fun getItemCount() = clothes.size

    private class HomeDiffUtil(
        private val oldList: List<Clothes>,
        private val newList: List<Clothes>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].itemCode == newList[newItemPosition].itemCode

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]

    }

    inner class HomeViewHolder(private val binding: ListHomeBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val resources: Resources = binding.root.resources

        init {
            binding.homeRVContainer.setOnClickListener(this)
        }

        fun bind(clothing: Clothes) {
            binding.homeRVCode.text = clothing.itemCode
            binding.homeRVStatus.text =
                resources.getString(R.string.homeRV_status, clothing.currentStatus)
            binding.homeRVPrice.text = resources.getString(
                R.string.homeRV_buyPrice,
                String.format("%.2f", clothing.purchasePrice)
            )
            val storageClothingReference =
                Firebase.storage.getReferenceFromUrl("$FIREBASE_STORAGE_LINK${clothing.imageReference}")

            GlideApp.with(binding.root)
                .load(storageClothingReference)
                .fitCenter()
                .placeholder(R.drawable.ic_cloth_100)
                .into(binding.homeRVImage)
        }


        override fun onClick(v: View?) {
            when (v?.id) {
                R.id.homeRV_container -> listener.clothingClicked(adapterPosition)
            }
        }
    }


    interface HomeClothingClicked {
        fun clothingClicked(index: Int)
    }
}