package com.gojol.notto.ui.option

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.databinding.ItemOptionLicenseBinding
import com.gojol.notto.model.data.License

class LicenseAdapter : RecyclerView.Adapter<LicenseAdapter.LicenseViewHolder>() {

    private var licenseList = listOf<License>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LicenseViewHolder {
        return LicenseViewHolder(
            ItemOptionLicenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LicenseViewHolder, position: Int) {
        holder.bind(licenseList[position])
    }

    override fun getItemCount(): Int = licenseList.size

    fun setLicenseList(newList: List<License>) {
        licenseList = newList
        notifyDataSetChanged()
    }

    class LicenseViewHolder(private val binding: ItemOptionLicenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: License) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}
