package com.example.wordsmatch

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.wordsmatch.databinding.ActivityGameOverOrderBinding

class GameOverOrder : AppCompatActivity() {

    private lateinit var binding: ActivityGameOverOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameOverOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val skor = intent.getIntExtra("skor", 0)
        val rekor = intent.getIntExtra("rekor", 0)

        binding.benarText.text = "Benar: $skor"
        binding.rekorText.text = "Rekor: $rekor"

        binding.mainLagi.setOnClickListener {
            startActivity(Intent(this, GameMenu::class.java))
            finish()
        }

        binding.menuUtama.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}