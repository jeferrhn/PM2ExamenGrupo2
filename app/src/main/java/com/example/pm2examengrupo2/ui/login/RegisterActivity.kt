package com.example.pm2examengrupo2.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pm2examengrupo2.data.network.ApiConfig
import com.example.pm2examengrupo2.data.repository.MainRepository
import com.example.pm2examengrupo2.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        ViewModelFactory(MainRepository(ApiConfig.getApiService()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        binding.btnRegister.setOnClickListener {
            val name = binding.etRegName.text.toString().trim()
            val email = binding.etRegEmail.text.toString().trim()
            val password = binding.etRegPassword.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(name, email, password)
        }

        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.registerResult.observe(this) { response ->
            if (response?.success == true) {
                Toast.makeText(this, response.message ?: "Registro exitoso", Toast.LENGTH_LONG).show()
                finish() // Regresa al login después del registro
            } else if (response != null) {
                Toast.makeText(this, response.message ?: "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
