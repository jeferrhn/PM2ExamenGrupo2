package com.example.pm2examengrupo2.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.pm2examengrupo2.databinding.ActivityMenuBinding
import com.example.pm2examengrupo2.ui.home.MainActivity
import com.example.pm2examengrupo2.ui.list.ContactsActivity
import com.example.pm2examengrupo2.ui.login.LoginActivity
import com.example.pm2examengrupo2.utils.SessionManager

class MenuActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        binding.cardCreate.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.cardList.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
