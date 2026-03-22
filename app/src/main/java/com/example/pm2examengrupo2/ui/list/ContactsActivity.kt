package com.example.pm2examengrupo2.ui.list

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pm2examengrupo2.data.model.Contact
import com.example.pm2examengrupo2.data.network.ApiConfig
import com.example.pm2examengrupo2.data.repository.MainRepository
import com.example.pm2examengrupo2.databinding.ActivityContactsBinding
import com.example.pm2examengrupo2.databinding.DialogUpdateContactBinding
import com.example.pm2examengrupo2.ui.login.ViewModelFactory
import com.example.pm2examengrupo2.ui.map.MapActivity
import com.example.pm2examengrupo2.utils.SessionManager

class ContactsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContactsBinding
    private lateinit var adapter: ContactsAdapter
    private lateinit var sessionManager: SessionManager
    private var allContacts: List<Contact> = emptyList()

    private val viewModel: ContactsViewModel by viewModels {
        ViewModelFactory(MainRepository(ApiConfig.getApiService()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        
        setupRecyclerView()
        setupSearch()
        setupObservers()

        val userId = sessionManager.getUserId()
        viewModel.fetchContacts(userId)
    }

    private fun setupRecyclerView() {
        adapter = ContactsAdapter(
            onContactClick = { contact ->
                showActionDialog(contact)
            },
            onOptionsClick = { contact ->
                showOptionsDialog(contact)
            }
        )
        
        binding.rvContacts.layoutManager = LinearLayoutManager(this)
        binding.rvContacts.setHasFixedSize(true)
        binding.rvContacts.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterContacts(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterContacts(query: String) {
        val filtered = allContacts.filter {
            it.name.contains(query, ignoreCase = true) || it.phone.contains(query)
        }
        adapter.submitList(filtered)
    }

    private fun setupObservers() {
        viewModel.contacts.observe(this) { contacts ->
            allContacts = contacts
            adapter.submitList(contacts)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.deleteResult.observe(this) { response ->
            if (response?.success == true) {
                Toast.makeText(this, "Eliminado con éxito", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.updateResult.observe(this) { response ->
            if (response?.success == true) {
                Toast.makeText(this, "Actualizado con éxito", Toast.LENGTH_SHORT).show()
            } else if (response != null) {
                Toast.makeText(this, response.message ?: "Error al actualizar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showActionDialog(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Acción")
            .setMessage("¿Desea ir a la ubicación de ${contact.name}?")
            .setPositiveButton("Sí") { _, _ ->
                val lat = contact.latitude ?: "0.0"
                val lon = contact.longitude ?: "0.0"

                if (lat != "0.0" && lat.isNotEmpty()) {
                    val intent = Intent(this, MapActivity::class.java).apply {
                        putExtra("latitud", lat)
                        putExtra("longitud", lon)
                        putExtra("nombre", contact.name)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Sin ubicación guardada", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showOptionsDialog(contact: Contact) {
        val options = arrayOf("Actualizar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Opciones: ${contact.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showUpdateDialog(contact)
                    1 -> confirmDelete(contact)
                }
            }
            .show()
    }

    private fun showUpdateDialog(contact: Contact) {
        val dialogBinding = DialogUpdateContactBinding.inflate(LayoutInflater.from(this))
        dialogBinding.etNombre.setText(contact.name)
        dialogBinding.etTelefono.setText(contact.phone)
        dialogBinding.etLatitud.setText(contact.latitude)
        dialogBinding.etLongitud.setText(contact.longitude)

        AlertDialog.Builder(this)
            .setTitle("Actualizar Contacto")
            .setView(dialogBinding.root)
            .setPositiveButton("Actualizar") { _, _ ->
                val newName = dialogBinding.etNombre.text.toString().trim()
                val newPhone = dialogBinding.etTelefono.text.toString().trim()
                val newLat = dialogBinding.etLatitud.text.toString().trim()
                val newLon = dialogBinding.etLongitud.text.toString().trim()

                if (newName.isEmpty() || newPhone.isEmpty()) {
                    Toast.makeText(this, "Faltan datos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                viewModel.updateContact(
                    sessionManager.getUserId(),
                    contact.id,
                    newName,
                    newPhone,
                    newLat,
                    newLon
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmDelete(contact: Contact) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar")
            .setMessage("¿Está seguro de eliminar a ${contact.name}?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.deleteContact(sessionManager.getUserId(), contact.id)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
