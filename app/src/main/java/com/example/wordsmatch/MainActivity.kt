//package com.example.wordsmatch
//
//import android.content.Intent
//import android.os.Bundle
//import android.widget.Button
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.wordsmatch.databinding.ActivityMainBinding
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
//        var rekors = getSharedPreferences("wordsmatch", MODE_PRIVATE)
//            .getInt("rekor", 0)
//
//        var bestScore = rekors
//        bestScore = intent.getIntExtra("rekor", rekors)
//        binding.tvBestScore.text = "Best score: $bestScore"
//
//        binding.buttonStart.setOnClickListener {
//            val intent = Intent(this, GameMenu::class.java)
//            startActivity(intent)
//        }
//
//    }
//}
//
package com.example.wordsmatch

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wordsmatch.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var namaUser = Session.username(this)
        if (namaUser == null) {
            namaUser = ""
        }
        binding.sapaUser.text = "Selamat bermain $namaUser !"

        binding.buttonStart.setOnClickListener {
            startActivity(Intent(this, GameMenu::class.java))
        }

        binding.buttonLeaderboard.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        binding.buttonKontak.setOnClickListener {
            if (Session.isLogin(this)) {
                tampilkanDialogAkun()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tampilkanBestScore()
    }

    private fun tampilkanBestScore() {
        val rekor = getSharedPreferences("wordsmatch", MODE_PRIVATE).getInt("rekor", 0)
        binding.tvBestScore.text = "Best score: $rekor"
    }

    // Kalau sudah login, ikon kontak menampilkan info akun + tombol logout
    private fun tampilkanDialogAkun() {
        AlertDialog.Builder(this)
            .setTitle(Session.username(this))
            .setMessage("Kamu sedang login.")
            .setPositiveButton("Logout") { _, _ ->
                Session.logout(this)
                tampilkanBestScore()
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}