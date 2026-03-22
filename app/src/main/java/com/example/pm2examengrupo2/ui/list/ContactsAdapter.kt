package com.example.pm2examengrupo2.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pm2examengrupo2.R
import com.example.pm2examengrupo2.data.model.Contact
import com.example.pm2examengrupo2.databinding.ItemContactBinding

class ContactsAdapter(
    private val onContactClick: (Contact) -> Unit,
    private val onOptionsClick: (Contact) -> Unit
) : ListAdapter<Contact, ContactsAdapter.ViewHolder>(ContactDiffCallback()) {

    // URL base para imágenes
    private val BASE_URL_IMAGES = "http://192.168.125.50/PM2ExamenApi/"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.tvContactName.text = contact.name
            binding.tvContactPhone.text = contact.phone

            // Cargar imagen
            if (!contact.photoPath.isNullOrEmpty()) {
                val fullUrl = if (contact.photoPath.startsWith("http")) {
                    contact.photoPath
                } else {
                    BASE_URL_IMAGES + contact.photoPath
                }

                Glide.with(binding.ivContactPhoto.context)
                    .load(fullUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .circleCrop()
                    .into(binding.ivContactPhoto)
            } else {
                binding.ivContactPhoto.setImageResource(android.R.drawable.ic_menu_report_image)
            }

            binding.root.setOnClickListener { onContactClick(contact) }
            binding.btnOptions.setOnClickListener { onOptionsClick(contact) }
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean = oldItem == newItem
    }
}
