package com.example.pm2examengrupo2.ui.signature

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import androidx.appcompat.app.AppCompatActivity
import com.example.pm2examengrupo2.databinding.ActivitySignatureBinding
import java.io.ByteArrayOutputStream

class SignatureActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignatureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignatureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnClear.setOnClickListener {
            binding.signatureView.clear()
        }

        binding.btnSave.setOnClickListener {
            val bitmap = binding.signatureView.getBitmap()
            val base64 = bitmapToBase64(bitmap)
            
            val intent = Intent().apply {
                putExtra("signature_base64", base64)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}