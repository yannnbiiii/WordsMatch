//package com.example.wordsmatch
//
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//
//class LoginActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_login)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}

package com.example.wordsmatch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.wordsmatch.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener { login() }

        binding.tvKeDaftar.setOnClickListener {
            startActivity(Intent(this, DaftarAkun::class.java))
            finish()
        }

        binding.tvLupaPassword.setOnClickListener {
            startActivity(Intent(this, LupaPasswordActivity::class.java))
        }
    }

    private fun login() {
        val username = binding.inputUsername.text.toString().trim()
        val password = binding.inputPassword.text.toString()

        binding.layoutUsername.error = null
        binding.layoutPassword.error = null

        if (username.isEmpty()) {
            binding.layoutUsername.error = "Username wajib diisi"
            return
        }
        if (password.isEmpty()) {
            binding.layoutPassword.error = "Password wajib diisi"
            return
        }

        binding.btnLogin.isEnabled = false

        lifecycleScope.launch {
            val res = ApiClient.post(
                "login.php",
                mapOf("username" to username, "password" to password)
            )
            binding.btnLogin.isEnabled = true

            if (res.optBoolean("success")) {
                Session.login(this@LoginActivity, username, res.optInt("best_score"))
                Toast.makeText(this@LoginActivity, "Selamat datang, $username", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // buang halaman-halaman di atas menu utama
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, res.optString("message"), Toast.LENGTH_SHORT).show()
            }
        }
    }
}