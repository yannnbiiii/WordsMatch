package com.example.wordsmatch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.wordsmatch.databinding.ActivityLupaPasswordBinding
import kotlinx.coroutines.launch

class LupaPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLupaPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLupaPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.btnCekUsername.setOnClickListener { cariPertanyaan() }
        binding.btnResetPassword.setOnClickListener { resetPassword() }
    }

    private fun cariPertanyaan() {
        val username = binding.inputUsername.text.toString().trim()
        if (username.isEmpty()) {
            binding.layoutUsername.error = "Masukkan username terlebih dahulu"
            return
        }

        binding.btnCekUsername.isEnabled = false

        lifecycleScope.launch {
            val res = ApiClient.post("get_question.php", mapOf("username" to username))
            binding.btnCekUsername.isEnabled = true

            if (res.optBoolean("success")) {
                val pertanyaan = res.optString("pertanyaan")
                binding.tvPertanyaan.text = "Pertanyaan: $pertanyaan"
                binding.containerReset.visibility = View.VISIBLE
                binding.inputUsername.isEnabled = false // kunci username
            } else {
                Toast.makeText(this@LupaPasswordActivity, res.optString("message"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword() {
        val username = binding.inputUsername.text.toString().trim()
        val jawaban = binding.inputJawaban.text.toString().trim()
        val passwordBaru = binding.inputPasswordBaru.text.toString()

        if (jawaban.isEmpty()) {
            binding.layoutJawaban.error = "Jawaban wajib diisi"
            return
        }
         if (passwordBaru.length < 6) {
            binding.layoutPasswordBaru.error = "Password minimal 6 karakter"
            return
        }

        binding.btnResetPassword.isEnabled = false

        lifecycleScope.launch {
            val res = ApiClient.post(
                "reset_password.php",
                mapOf(
                    "username" to username,
                    "jawaban" to jawaban,
                    "password_baru" to passwordBaru
                )
            )
            binding.btnResetPassword.isEnabled = true

            Toast.makeText(this@LupaPasswordActivity, res.optString("message"), Toast.LENGTH_SHORT).show()

            if (res.optBoolean("success")) {
                // Kembali ke halaman Login
                startActivity(Intent(this@LupaPasswordActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}