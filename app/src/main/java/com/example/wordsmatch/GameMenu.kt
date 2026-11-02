package com.example.wordsmatch

import android.R.attr.fontFamily
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.example.wordsmatch.databinding.ActivityGameMenuBinding

class GameMenu : AppCompatActivity() {

    private lateinit var binding: ActivityGameMenuBinding

    fun Int.pxToDp(context: Button): Float {
        val density = context.resources.displayMetrics.density
        return (this * density)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityGameMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Function
        fun UpdateNyawa(nyawa: Int){
            binding.nyawa.text = "❤".repeat(nyawa)
        }

        val bestScore = 64
        val currentBenar = 4
        var nyawa = 3
        UpdateNyawa(nyawa)
        binding.kurangiNyawa.setOnClickListener {
            nyawa = nyawa - 1
            UpdateNyawa(nyawa)
        }
        binding.benarSaatIni.text = getString(R.string.benar_saat_ini, currentBenar)
        binding.rekorSaatIni.text = getString(R.string.rekor_text, bestScore)

        // Game core
        val div2btn = findViewById<LinearLayout>(R.id.div2btn)
        val daftarKata = listOf("Whatever","Apapun","Table","Meja","Wise","Bijak", "Most", "Banyak", "Could", "Bisa").shuffled()
        div2btn.removeAllViews()

        for(i in daftarKata.indices step 2) {
            val kataPertama = daftarKata[i]
            val kataKedua = daftarKata[i + 1]

            val barisLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0,8,0,8)
                }
            }

            val arcaFont = androidx.core.content.res.ResourcesCompat.getFont(this, R.font.arca_majora_3_bold)
            val lebarPx = (140 * resources.displayMetrics.density).toInt()

            val tombol1 = Button(this).apply {
                text = kataPertama
                textSize = 18f
                backgroundTintList = getColorStateList(R.color.main_color)
                typeface = arcaFont
                updatePadding(0,0,0,14)
                setTextColor(getColor(android.R.color.white))

                layoutParams = LinearLayout.LayoutParams(
                    lebarPx,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            val spacer = android.widget.Space(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    1
                ).apply {
                    weight = 1f
                }
            }

            val tombol2 = Button(this).apply {
                text = kataKedua
                textSize = 18f
                backgroundTintList = getColorStateList(R.color.main_color)
                updatePadding(0,0,0,14)
                setTextColor(getColor(android.R.color.white))
                typeface = arcaFont

                layoutParams = LinearLayout.LayoutParams(
                    lebarPx,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            barisLayout.addView(tombol1)
            barisLayout.addView(spacer)
            barisLayout.addView(tombol2)

            div2btn.addView(barisLayout)
        }


    }


}