package com.example.pm2examengrupo2.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.pm2examengrupo2.R
import com.example.pm2examengrupo2.data.network.ApiConfig
import com.example.pm2examengrupo2.data.repository.MainRepository
import com.example.pm2examengrupo2.databinding.ActivityMainBinding
import com.example.pm2examengrupo2.ui.list.ContactsActivity
import com.example.pm2examengrupo2.ui.login.LoginActivity
import com.example.pm2examengrupo2.ui.login.ViewModelFactory
import com.example.pm2examengrupo2.ui.signature.SignatureActivity
import com.example.pm2examengrupo2.utils.SessionManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sessionManager: SessionManager

    private var fotoBase64: String = ""
    private var firmaBase64: String = ""

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory(MainRepository(ApiConfig.getApiService()))
    }

    private val signatureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val base64 = result.data?.getStringExtra("signature_base64") ?: ""
            if (base64.isNotEmpty()) {
                firmaBase64 = base64
                try {
                    val imageBytes = Base64.decode(firmaBase64, Base64.DEFAULT)
                    val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    binding.ivFirma.setImageBitmap(decodedImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            @Suppress("DEPRECATION")
            val imageBitmap = result.data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                binding.ivFoto.setImageBitmap(imageBitmap)
                fotoBase64 = bitmapToBase64(imageBitmap, 50) 
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            setSupportActionBar(binding.toolbar)
            
            sessionManager = SessionManager(this)
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            setupListeners()
            setupObservers()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            sessionManager.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListeners() {
        binding.btnCapturarFirma.setOnClickListener {
            val intent = Intent(this, SignatureActivity::class.java)
            signatureLauncher.launch(intent)
        }

        binding.btnTomarFoto.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
            } else {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(intent)
            }
        }

        binding.btnGetLocation.setOnClickListener {
            getLastLocation()
        }

        binding.btnSalvar.setOnClickListener {
            val name = binding.etNombre.text?.toString()?.trim() ?: ""
            val phone = binding.etTelefono.text?.toString()?.trim() ?: ""
            val email = binding.etDescripcion.text?.toString()?.trim() ?: ""
            val lat = binding.etLatitud.text?.toString() ?: "0.0"
            val lon = binding.etLongitud.text?.toString() ?: "0.0"

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Nombre y teléfono son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fotoBase64.isEmpty() || firmaBase64.isEmpty()) {
                Toast.makeText(this, "La foto y la firma son obligatorias", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sessionManager.getUserId()
            viewModel.saveContact(userId, name, phone, email, lat, lon, fotoBase64, firmaBase64)
        }

        binding.btnVerContactos.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 102)
            return
        }
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                binding.etLatitud.setText(location.latitude.toString())
                binding.etLongitud.setText(location.longitude.toString())
            } else {
                Toast.makeText(this, "GPS desactivado o sin señal.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.saveResult.observe(this) { response ->
            if (response?.success == true) {
                Toast.makeText(this, "Contacto guardado correctamente", Toast.LENGTH_SHORT).show()
                clearFields()
            } else {
                Toast.makeText(this, response?.message ?: "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.btnSalvar.isEnabled = !isLoading
            binding.btnSalvar.text = if (isLoading) "GUARDANDO..." else "Salvar Contacto"
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap, quality: Int): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun clearFields() {
        binding.etNombre.setText("")
        binding.etTelefono.setText("")
        binding.etDescripcion.setText("")
        binding.etLatitud.setText("")
        binding.etLongitud.setText("")
        binding.ivFoto.setImageResource(0)
        binding.ivFirma.setImageResource(0)
        fotoBase64 = ""
        firmaBase64 = ""
    }
}
