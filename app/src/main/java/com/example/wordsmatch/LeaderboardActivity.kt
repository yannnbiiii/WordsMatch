package com.example.wordsmatch

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.wordsmatch.databinding.ActivityLeaderboardBinding
import com.example.wordsmatch.databinding.ItemLeaderboardBinding
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        muatLeaderboard()
    }

    private fun muatLeaderboard() {
        binding.tvStatus.text = "Memuat..."

        lifecycleScope.launch {
            val res = ApiClient.post("leaderboard.php")

            if (!res.optBoolean("success")) {
                binding.tvStatus.text = res.optString("message")
                return@launch
            }

            val data = res.getJSONArray("data")
            binding.tvStatus.text = if (data.length() == 0) "Belum ada skor" else ""
            binding.listLeaderboard.removeAllViews()

            for (i in 0 until data.length()) {
                val item = data.getJSONObject(i)
                val row = ItemLeaderboardBinding.inflate(layoutInflater, binding.listLeaderboard, false)
                row.tvRank.text = "${i + 1}."
                row.tvNama.text = item.getString("username")
                row.tvSkor.text = item.getInt("best_score").toString()
                binding.listLeaderboard.addView(row.root)
            }
        }
    }
}