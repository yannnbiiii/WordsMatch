//package com.example.wordsmatch
////
////import android.os.Bundle
////import androidx.activity.enableEdgeToEdge
////import androidx.appcompat.app.AppCompatActivity
////import androidx.core.view.ViewCompat
////import androidx.core.view.WindowInsetsCompat
////
////class DaftarAkun : AppCompatActivity() {
////    override fun onCreate(savedInstanceState: Bundle?) {
////        super.onCreate(savedInstanceState)
////        enableEdgeToEdge()
////        setContentView(R.layout.activity_daftar_akun)
////        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
////            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
////            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
////            insets
////        }
////    }
////}
//
package com.example.wordsmatch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.wordsmatch.databinding.ActivityDaftarAkunBinding
import kotlinx.coroutines.launch

class DaftarAkun : AppCompatActivity() {

    private lateinit var binding: ActivityDaftarAkunBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDaftarAkunBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.btnDaftar.setOnClickListener { daftar() }

        binding.tvKeLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun daftar() {
        val username = binding.inputUsername.text.toString().trim()
        val password = binding.inputPassword.text.toString()
        val pertanyaan = binding.inputPertanyaan.text.toString().trim()
        val jawaban = binding.inputJawaban.text.toString().trim()

        binding.layoutUsername.error = null
        binding.layoutPassword.error = null
        binding.layoutPertanyaan.error = null
        binding.layoutJawaban.error = null

        if (username.isEmpty()) {
            binding.layoutUsername.error = "Username wajib diisi"
            return
        }
        if (password.length < 6) {
            binding.layoutPassword.error = "Password minimal 6 karakter"
            return
        }
        if (pertanyaan.isEmpty()) {
            binding.layoutPertanyaan.error = "Pertanyaan keamanan wajib diisi"
            return
        }
        if (jawaban.isEmpty()) {
            binding.layoutJawaban.error = "Jawaban keamanan wajib diisi"
            return
        }

        binding.btnDaftar.isEnabled = false // cegah klik berkali-kali

        lifecycleScope.launch {
            val res = ApiClient.post(
                "register.php",
                mapOf(
                    "username" to username,
                    "password" to password,
                    "pertanyaan" to pertanyaan,
                    "jawaban" to jawaban
                )
            )
            binding.btnDaftar.isEnabled = true
            Toast.makeText(this@DaftarAkun, res.optString("message"), Toast.LENGTH_SHORT).show()

            if (res.optBoolean("success")) {
                startActivity(Intent(this@DaftarAkun, LoginActivity::class.java))
                finish()
            }
        }
    }
}